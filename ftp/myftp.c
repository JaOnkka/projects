//client code
#include"myftp.h"
/* Jake Onkka
 * Client file
 * Valid Commands: exit,cd<path>,rd<path>,ls,rls,get<path>,show<path>,put<path>
 * 
 *
 *
 */
int makedata(char *myhost, int serversock);
int mymore(int datasock);
int myget(char *myhost, int serversock, char *pathname);
int filetype(char *pathname);
void myerror(char *error);
char *fileparse(char *pathname);
char *readparse(char *response);
int debug=0;	//debug flag
void main(int argc, char *argv[]){
	if(argc == 2 || argc == 3){
		char portstring[10];
		if(argc==3){
			if(strcmp(argv[2],"-d")==0){
				debug=1;		//check debug flag for more output
			}
		}
		int datasock;
		sprintf(portstring,"%d",myport);
		int serversock, rv, err;
		struct sockaddr_in servaddr;
		struct addrinfo hints, *actualdata;	//creating serversocket
		memset(&hints,0,sizeof(hints));
		char hostName[NI_MAXHOST];
		hints.ai_family=AF_INET;
		hints.ai_socktype=SOCK_STREAM;
		err = getaddrinfo(argv[1],portstring,&hints,&actualdata);
		printf("%s\n",argv[1]);
		if(err != 0){
			fprintf(stderr,"Error: %s\n",gai_strerror(err));
		 	exit(1);	
		}
		serversock = socket(actualdata->ai_family,actualdata->ai_socktype,0);
		if(serversock < 0){
			fprintf(stderr,"Error: %s\n",strerror(errno));
		 	exit(1);	
		} 

//		printf("connecting\n");
		rv = connect(serversock,actualdata->ai_addr,actualdata->ai_addrlen);	//connect
		if(rv < 0){
			fprintf(stderr,"Error: %s\n",strerror(errno));    
		     	exit(1);	
		}
		if(debug)printf("Connected\n");
		//client is connected to server
		char input[pathsize];	//commands from stdin
		char *command, *pathname, *buf;	//buffers for reading and writing to socket
		char response[bufsize];		//response is read in from server
		int actual,fd;		//used for reading and opening files
		char *filename;	
	for(;;){		//infinite loop, every break returns here
		for(;;){
			printf("<MYFTP>: ");	//print this for every user prompt
			fflush(stdout);
			read(0,input,pathsize);		//read stdin
			command = strtok(input," ");	//first word assume is command
			pathname = strtok(NULL," ");	//second word if any is the pathname
			for(int i = 0; i < strlen(command); i++){	//null terminate the command
				if(command[i]=='\n'){
					command[i]='\0';
				}
			}
			if(pathname!=NULL){			//null terminate path if any
				for(int i = 0; pathname[i]!='\0'; i++){
					if(pathname[i]=='\n'){
						pathname[i]='\0';
					}
				}
			}

			if(strcmp(command,"exit")==0){		//exit
				write(serversock,"Q\n",2);	//write Q to server
				if(debug)printf("Sent:Q!\n");
				fflush(stdout);
				read(serversock,response,bufsize);	//recieve A\n
				strcpy(response,readparse(response));	//server has exited
				if(debug)printf("Response:%s!\n",response);
				fflush(stdout);
				exit(1);		//exit
			}
			if(strcmp(command,"cd")==0){		//change client directory
				rv = chdir(pathname);
				if(rv < 0){
					fprintf(stderr,"%s",strerror(errno));
					break;
				}
			}
			if(strcmp(command,"rcd")==0){		//change server directory
				if(pathname==NULL){
					myerror("Invalid use of rcd");
					break;
				}
				write(serversock,"C",1);
				write(serversock,pathname,strlen(pathname));	//write C<path>\n
				write(serversock,"\n",1);
				if(debug)printf("Sent:C%s!\n",pathname);
				fflush(stdout);

				read(serversock,response,bufsize);	//read server response
				strcpy(response,readparse(response));
				if(debug)printf("Response:%s!\n",response);
				fflush(stdout);
				if(response[0]=='E'){
					myerror(response);
					break;
				}
			}

			if(strcmp(command,"ls")==0){	//ls client directory
				int fd[2];
				rv = pipe(fd);		//pipe
				if(pipe < 0){
					myerror(strerror(errno));
					break;
				}
				int status1,status2;
				pid_t child1,child2;
				child1=fork();		//fork
				int cpy=dup(0);		//save stdin stdout
				int cpy2=dup(1);
				if(child1==0){		//child 1 runs ls, WRITES
					dup2(fd[1],1);
					close(fd[1]);
					close(fd[0]);
					execlp("ls","ls","-l",NULL);
				}else{
					child2=fork();		//child 2 runs more, READS
					if(child2==0){
						dup2(fd[0],0);
						close(fd[0]);
						close(fd[1]);
						execlp("more","more","-20",NULL);
					}
				}
				close(fd[0]);
				close(fd[1]);		//close pipe
				wait(&status1);		//wait for children to die
				wait(&status2);
				if(debug)printf("Finished outputting data!\n");
				fflush(stdout);
			}
			if(strcmp(command,"rls")==0){		//ls on server side
				datasock = makedata(argv[1],serversock);	//make datasocket
				write(serversock,"L\n",2);		//write L\n
				if(debug)printf("Sent:L!\n");
				fflush(stdout);
				read(serversock,response,bufsize);	//read response
				strcpy(response,readparse(response));
				if(debug)printf("Response:%s!\n",response);
				fflush(stdout);
				if(response[0]=='E'){
					fprintf(stderr,"%s\n",response);
					break;
				}
			
				rv = mymore(datasock);		//run more from datasock
				if(rv  < 0){
					break;
				}
				
			}
			if(strcmp(command,"get")==0){		//get file from server, create new file
				if(pathname==NULL){
					myerror("Invalid use of get");
					break;
				}
				if(fileparse(pathname)==NULL){		//get last part of pathname ex: /user/jake.onkka/file.txt
					myerror("Invalid file name!");
					break;
				}
				strcpy(pathname,fileparse(pathname));
				if(debug)printf("recievedfilename:%s!%lu\n",pathname,strlen(pathname));
				fflush(stdout);
				rv = filetype(pathname);		//check if file exists
				if(rv > 0){
					myerror("File already exists");
					break;
				}
				
				datasock = makedata(argv[1],serversock);	//make data connection, is either sock or err
				if(datasock < 0){
					close(datasock);
					break;
				}
				rv = myget(argv[1],serversock,pathname);	//run get command
				if(rv < 0){
					close(datasock);
					break;
				}

				fd = open(pathname,O_WRONLY | O_CREAT | O_EXCL, 0755);	//if file already exists, return -1			
				if(fd < 0){
					myerror(strerror(errno));
					close(datasock);
					break;
				}
				while((actual=read(datasock,response,bufsize)) > 0){	//if success we write to file
					write(fd,response,actual);
				}
				
					close(datasock);
			}

			if(strcmp(command,"show")==0){			//similar to get, except run ls on the file
				if(pathname==NULL){
					myerror("Invalid use of show");
					break;
				}
		
				datasock = makedata(argv[1],serversock);	//make data connection, is either sock or err
				if(datasock < 0){
					close(datasock);
					break;
				}
				rv = myget(argv[1],serversock,pathname);	//run get command
				if(rv < 0){
					close(datasock);
					break;
				}
				rv = mymore(datasock);				//run more on datasock
				if(rv < 0){
					close(datasock);
					break;
				}	
					close(datasock);
			}
			if(strcmp(command,"put")==0){				//opposite of get, write file to server
				if(pathname==NULL){
					myerror("Invalid use of show");
					break;
				}
				if(debug)printf("doingput:%s!\n",pathname);
				fflush(stdout);
				rv = filetype(pathname);			//check if file exists
				if(rv != 1){
					fprintf(stderr,"Not a regular file\n");
					break;
				}

				fd = open(pathname, O_WRONLY, 0755);	//if file already exists, return -1			
				if(fd < 0){
					myerror(strerror(errno));
					break;
				}

				datasock = makedata(argv[1],serversock);	//make data connection
				if(datasock < 0){
					close(datasock);
					break;
				}
				write(serversock,"P",1);			//write P<path>\n
				write(serversock,pathname,pathsize);
				write(serversock,"\n",1);
				if(debug)printf("Sent:P%s!\n",pathname);
				fflush(stdout);
				read(serversock,response,bufsize);		///read acknowledgement
				strcpy(response,readparse(response));
				if(debug)printf("Response:%s!\n",response);
				fflush(stdout);
				if(response[0]=='E'){
					fprintf(stderr,"%s\n",response);
					close(datasock);
					break;
				}
				while((actual = read(fd,input,bufsize)) > 0){	//if accepted then we write file to server
					write(datasock,input,actual);
				}
				if(debug)printf("Finished writing to server!\n");
				fflush(stdout);
				close(fd);
				close(datasock);
				
			}

		}//for

	}//1st for
	}
}
//function for get, sends G<path> to server
//return -1 on error, return 1 on success
int myget(char *myhost, int serversock, char *pathname){
	int datasock,actual;
	char response[bufsize];		
	write(serversock,"G",1);
	write(serversock,pathname,strlen(pathname));
	write(serversock,"\n",1);
	if(debug)printf("Sent:G%s!\n",pathname);
	fflush(stdout);
	actual=read(serversock,response,pathsize);
	strcpy(response,readparse(response));
	if(debug)printf("Response:%s!%d\n",response,actual);
	fflush(stdout);
	if(response[0]=='E'){
		myerror(response);
		return -1;
	}
	return 1;
}
//function to parse filename
//returns the last part of a pathname 
//ex: path=/home/users/jake/sample.txt
//returns: sample.txt
char *fileparse(char *pathname){
	char response[bufsize];
	char *filename;
	if(debug)printf("Pathname:%s!%lu\n",pathname,strlen(pathname));
	fflush(stdout);
	int slashes=0;
	for(int i = 0; i < strlen(pathname); i++){	//count how many slashes in pathname
		if(pathname[i]=='/'){
			slashes++;
		}
	}
	if(slashes == 0){		//if no slashes, just return that pathname
		if(debug)printf("Donepathname:%s!%lu\n",pathname,strlen(pathname));
		fflush(stdout);
		return(pathname);
	}
	if(slashes == 1){		//if one slash
			if(pathname[0]=='/'){		//if file has one slash and it starts with slash, complain, dont want file named /example
				if(debug)printf("Unknown:%s!\n",pathname);
				fflush(stdout);
				return NULL;
			}
		filename=strtok(pathname,"/");		//parse just the one slash
		filename=strtok(NULL,"/");
		if(debug)printf("Only1filename:%s!%lu\n",filename,strlen(filename));
		fflush(stdout);
		return(filename);
	}

	if(slashes > 1){		//for more slashes
		filename=strtok(pathname,"/");
		if(debug)printf("Dones1filename:%s!%lu\n",filename,strlen(filename));
		fflush(stdout);
	}
	if(slashes > 1){		//strtok for every slash
		for(int i = 0; i < slashes-1; i++){
			filename=strtok(NULL,"/");
			if(debug)printf("filename%d:%s!%lu\n",i,filename,strlen(filename));
			fflush(stdout);
		}
	}
	if(debug)printf("Donefilename:%s!%lu\n",filename,strlen(filename));
	fflush(stdout);
	return(filename);

}
//runs more command
//forks child process into execing more and feeding input through pipe
int mymore(int datasock){
	int fd[2];
	int status1, status2;
	pid_t child1, child2;
	if(pipe(fd)>0){
		fprintf(stderr,"%s",strerror(errno));
		return -1;
	}
	child1=fork();		//fork
	if(child1 == 0){		//child 1 writes
		char buffer[pathsize];
			dup2(fd[1],1);
			close(fd[0]);
		int attempt;
		while((attempt=read(datasock,buffer,pathsize)) > 0){
			write(1,buffer,attempt);
		}

		exit(1);
	}
	else{
		child2=fork();
		if(child2==0){		//child 2 reads, exec more -20
			dup2(fd[0],0);
			close(fd[1]);
			execlp("more","more","-20",NULL);
			exit(1);
		}
			close(fd[0]);
			close(fd[1]);
			wait(&status1);	//parent waits for children to die
			wait(&status2);
	}

	if(debug)printf("Finished outputting data!\n");
	fflush(stdout);
	return 1;
}
//makes data connection to server
//returns either -1 for error, or datasockfd
int makedata(char *myhost, int serversock){

	char response[bufsize];
	write(serversock,"D\n",2);		//send D\n
	if(debug)printf("Sent:D!\n");
	fflush(stdout);
	read(serversock,response,bufsize);	//read response
	strcpy(response,readparse(response));
	if(debug)printf("Response:%s!\n",response);
	if(response[0]=='E'){
		myerror(response);
		return -1;
	}
	if(response[0]=='A'){

		for(int i = 0; i < 7; i++){		//response comes with A followed by 5 ints for port number
			if(response[i+1]=='\n'){
				response[i+1]='\0';
			}
			response[i]=response[i+1];
		}
		char newresponse[10];
		strncpy(newresponse,response,6);		//strcpy port #

		if(debug)printf("newResponse:%ld:%s!\n",strlen(newresponse),newresponse);
		int datasock,rv;

	
		struct sockaddr_in servaddrs;
		struct addrinfo hints, *actualdata;	//create new socket and connection
		memset(&hints,0,sizeof(hints));
		char hostName[NI_MAXHOST];
		hints.ai_family=AF_INET;
		hints.ai_socktype=SOCK_STREAM;
		int err = getaddrinfo(myhost,newresponse,&hints,&actualdata);
		if(err != 0){
			fprintf(stderr,"Error: %s\n",gai_strerror(err));
			return -1;
		}
		datasock=socket(actualdata->ai_family,actualdata->ai_socktype,0);
		if(datasock < 0){
			fprintf(stderr,"Error: %s\n",strerror(errno));
			return -1;
		}
		rv = connect(datasock,actualdata->ai_addr,actualdata->ai_addrlen);	//connect to datasocket
		if(rv < 0){
			fprintf(stderr,"Error: %s\n",strerror(errno));
			return -1;
		}
		if(debug)printf("DataConnection!\n");
		fflush(stdout);
		return datasock;
	}
	return -1;


}
//checks if server respons with error
//return < 0 if is an error, 1 if acknowledgement(valid)
void myerror(char *error){
	fprintf(stderr,"%s\n",error);
	fflush(stderr);
}
//parse input and null terminate
char *readparse(char *response){
	for(int i = 0; i < strlen(response); i++){
		if(response[i]=='\n'){
			response[i]='\0';
			return response;
		}
	}
	return response;
}
//runs stat to check if file, directory, or neither
//return 1 for file, 2 for directory, -1 neither
int filetype(char *pathname){
	struct stat *file;
	if(stat(pathname,file)==0){
		if(S_ISREG(file->st_mode)){
			return 1;
		}
		else if(S_ISDIR(file->st_mode)){
			return 2;
		}
		else{
			myerror(strerror(errno));
			return -1;
		}
	}
	if(stat(pathname,file) < 0){
		myerror(strerror(errno));
		return -1;
	}
}
