//server code
#include"myftp.h"
/* Jake Onkka
 * Server code
 * Reads commands from client
 * Executes commands and sends back acknowledgement or error, format A\n or E\n
 *
 */
int dataConnection(int connectfd);
int myerror(int connectfd, char *message);
int filetype(int connectfd, int fd);
int parse(int connectfd);
	int debug=0;
void main(int argc, char *argv[]){
	if(argc==2){
	if(strcmp(argv[1],"-d")==0){
		debug=1;
	}
	}
	struct sockaddr_in servaddr, clientaddr, nameaddr;
	char buf[pathsize];
	char pathname[pathsize];	//string containing pathname sent from client
	int isdataconnection=0;		//0 false, 1 true, used to check if data connection has been established
	struct stat mystat;		//stat used to verify files
	int hostEntry, mysock, clientsock, serverPort, length, rv, newPort, dataconnectfd, pid, actual;
	mysock = socket(AF_INET,SOCK_STREAM,0);
	if(mysock < 0){ fprintf(stderr,"Error: %s\n",strerror(errno));exit(1);}
	rv = setsockopt(mysock,SOL_SOCKET,SO_REUSEADDR,&(int){1},sizeof(int));
	if(rv < 0){ fprintf(stderr,"Error: %s\n",strerror(errno));exit(1);}

	servaddr.sin_family = AF_INET;
	servaddr.sin_addr.s_addr = htonl(INADDR_ANY);
	servaddr.sin_port = htons(myport); //HTONSa

	rv = bind(mysock,(struct sockaddr*)&servaddr,sizeof(servaddr));
	if(rv < 0){ fprintf(stderr,"Error; %s\n",strerror(errno));exit(1);}

	listen(mysock,4);	//LISTEN FOR CONNECTIONS
	int status;
	int connectfd;		//read and write from this

	for(;;){
		length = sizeof(struct sockaddr_in);
		connectfd = accept(mysock,(struct sockaddr*)&clientaddr,&length);	//makes connection
		if(connectfd < 0){  fprintf(stderr,"Error: %s\n",strerror(errno));exit(1);}
		char hostName[NI_MAXHOST];
		int hostEntry;
		hostEntry = getnameinfo((struct sockaddr*)&clientaddr,sizeof(clientaddr),hostName,sizeof(hostName),NULL,0,NI_NUMERICSERV);	
		if(hostEntry != 0){ fprintf(stderr,"Error: %s\n",gai_strerror(hostEntry));exit(1);}
		printf("Client joined: %s\n",hostName);
		if((pid = fork())==0){	//server makes child process to handle incoming clients then go back to waiting for more clients to connect
			close(mysock);
			//write to connectfd
			char cmd[pathsize];
			for(;;){

				rv = read(connectfd,cmd,bufsize);
				for(int i = 0; i < strlen(cmd); i++){	//replace newline with null byte for strcmp
					if(cmd[i]=='\n'){
						cmd[i]='\0';
					}

				}
				switch(cmd[0]){		//switch case to tell what command user input
				case'Q':	//user wants to exit
					if(debug)printf("DConnection=%d\n",isdataconnection); fflush(stdout);
					if(debug) printf("Recieved:%c!\n",cmd[0]);
					write(connectfd,"A\n",2);	//acknowledge
					if(debug) printf("Sent:A\n");
					printf("(%d) Exiting\n",getpid());
					exit(0);	//exit
					break;
				case'D':	//data connection

					if(debug) printf("Recieved:%c!\n",cmd[0]);
					if(isdataconnection){
						myerror(connectfd,"Data connection has already been established");
						if(debug)printf("DConnection=%d\n",isdataconnection); fflush(stdout);
						break;
					}
					dataconnectfd = dataConnection(connectfd);	//run data command to create new port #
					if(dataconnectfd < 0){
						close(dataconnectfd);
						isdataconnection=0;
						if(debug)printf("DConnection=%d\n",isdataconnection); fflush(stdout);
						break;
					}
					isdataconnection = 1;
						if(debug)printf("DConnection=%d\n",isdataconnection); fflush(stdout);
					break;
				case'C':	//change directory
					if(debug) printf("Recieved:%c!\n",cmd[0]);
					rv = read(connectfd,pathname,pathsize);		//read pathname
					if(debug) printf("Recieved:%s!\n",pathname);
					for(int i = 0; i < strlen(pathname); i++){		//null terminate pathname
						if(pathname[i]=='\n'){
							pathname[i]='\0';
						}
					}
					printf("(%d) Command: C %s\n",getpid(),pathname);
					rv = chdir(pathname);	
					if(rv < 0){		//error if not a valid directory
						fprintf(stderr,"(%d) cd to %s failed with error: %s\n",getpid(),pathname,strerror(errno));
						myerror(connectfd,strerror(errno));
						break;
					}
					write(connectfd,"A\n",2);	//acknowledge
					if(debug) printf("Sent:A!\n");
					printf("(%d) Changed current directory to %s\n",getpid(),pathname);
					break;
				case'G':    //get OR show, write a file to Dsocket
					if(debug) printf("Recieved:%c!\n",cmd[0]);
					if(!isdataconnection){
						myerror(connectfd,"Data connection has not been established");
						if(debug)printf("DConnection=%d\n",isdataconnection); fflush(stdout);
						break;
					}
					rv = read(connectfd,pathname,pathsize);		//read pathname
					if(debug) printf("Recieved:%s!\n",pathname);
					for(int i = 0; i < strlen(pathname); i++){	//replace newline with null byte for strcmp
						if(pathname[i]=='\n'){
							pathname[i]='\0';
						}
					}
					printf("(%d) Command: G %s\n",getpid(),pathname);
					printf("(%d) Reading file %s\n",getpid(),pathname);
					int fd;

					fd = open(pathname, O_RDONLY,0);	//open file with rdonly permissions
					if(fd < 0){			//error if file doesn't exist or no perms
						myerror(connectfd,strerror(errno));
						close(dataconnectfd);
						isdataconnection=0;
						if(debug)printf("DConnection=%d\n",isdataconnection);
						fflush(stdout);
						printf("(%d) Error:	%s\n",getpid(),strerror(errno));
						break;
					}
					rv = filetype(connectfd,fd);	//check file
					if(rv != 1){
						myerror(connectfd,"Not regular file");
						close(dataconnectfd);
						isdataconnection=0;
						printf("(%d) Error:	Not regular file\n",getpid());
						if(debug)printf("DConnection=%d\n",isdataconnection);
						fflush(stdout);
						break;
					}
					write(connectfd,"A\n",2);	//acknowledge file has been opened
					if(debug) printf("Sent:A!\n");
					char buf[bufsize];
					while((actual = read(fd,buf,bufsize)) > 0){	//write file to client
						write(dataconnectfd,buf,actual);
					}
					printf("(%d) Transmitted file %s to client\n",getpid(),pathname);
					close(fd);
					close(dataconnectfd);
					isdataconnection=0;
						if(debug)printf("DConnection=%d\n",isdataconnection); fflush(stdout);
					break;
				case'P':	//put command
					if(debug) printf("Recieved:%c!\n",cmd[0]);
					if(!isdataconnection){
						myerror(connectfd,"Data connection has not been established");
						break;
					}
					rv = read(connectfd,pathname,pathsize);		//read pathname
					if(debug) printf("Recieved:%s!\n",pathname);
					for(int i = 0; i < strlen(pathname); i++){	//replace newline with null byte for strcmp
						if(pathname[i]=='\n'){
							pathname[i]='\0';
						}
					}
					if(debug)printf("pathname=%s\n",pathname);
					fd = open(pathname, O_WRONLY | O_CREAT | O_EXCL, 0755);		//check file doesn't exist
					if(debug)printf("fd=%d\n",fd);
					if(fd < 0){
						myerror(connectfd,strerror(errno));
						close(dataconnectfd);
						if(debug)printf("myerr=%s\n",strerror(errno));
						isdataconnection=0;
						if(debug)printf("DConnection=%d\n",isdataconnection);
						fflush(stdout);
						printf("(%d) Error:	%s\n",getpid(),strerror(errno));
						break;
					}

					printf("(%d) Writing file %s\n",getpid(),pathname);
					write(connectfd,"A\n",2);		//acknowledge
					if(debug) printf("Sent:A!\n");

					while((actual=read(dataconnectfd,buf,bufsize)) > 0){	//write new file
						write(fd,buf,actual);
					}
					close(fd);
					close(dataconnectfd);
					isdataconnection=0;
					printf("(%d) Finished writing to file\n",getpid());
					break;
					
				case'L':     //rls, fails if no Dsocket, fork to exec ls -l command and piped into Dsocket
					if(debug) printf("Recieved:%c!\n",cmd[0]);
					if(!isdataconnection){
						myerror(connectfd,"Data connection has not been established");
						break;
					}
					printf("(%d) Command: L\n",getpid());
					write(connectfd,"A\n",2);		//acknowledge
					if(debug) printf("Sent:A!\n");
					int cpy=dup(1);
					dup2(dataconnectfd,1);		//dup stdout into dataconnectfd
					if(fork()==0){		//fork child
						execlp("ls","ls","-l",NULL);	//execute ls -l, writes to dataconnectfd
					}
					wait(NULL);
					close(dataconnectfd);
					isdataconnection=0;
						if(debug)printf("DConnection=%d\n",isdataconnection); fflush(stdout);
					dup2(cpy,1);
					printf("(%d) Finished sending data\n",getpid());
					break;
				
				}
				
			}
				
			close(connectfd);
			exit(0);
		}//end of child

		close(connectfd);	//server closes connectfd and then makes another for each client
		waitpid(-1,&status, WNOHANG);	//kill zombies if given chance
	}
}
//parse input
//get rid of newline
int parse(int connectfd){
	if(debug) printf("Using Parse:!\n");
	char argument[pathsize];
	int rv = read(connectfd,argument,pathsize);
	if(debug) printf("Recieved:%s!\n",argument);
	for(int i = 0; i < strlen(argument); i++){
	if(argument[i]=='\n'){
		argument[i]='\0';
	}
	}
	int arg=atoi(argument);
	//printf("In argument,%d",arg);
	return(arg);
}
//check filetype
//return 1 for file, 2 directory, -1 neither
int filetype(int connectfd, int fd){
	struct stat *file;
	if(fstat(fd,file)==0){
		if(S_ISREG(file->st_mode)){	//is a regular file
			return 1;
		}
		else if(S_ISDIR(file->st_mode)){	//is a directory
			return 2;
		}
		else{		//is not a regular file or a directory, so we dont care
			myerror(connectfd, strerror(errno));
			return -1;
		}
	}
	if(fstat(fd,file) < 0){
		myerror(connectfd, strerror(errno));
	}
	return -1;
}
//function used to write custom error message to client
int myerror(int connectfd, char *message){
	char *error;
	write(connectfd,"E",1);
	write(connectfd,message,strlen(message));
	write(connectfd,"\n",1);
	if(debug) printf("Sent:E%s!\n",error);
	fflush(stdout);
	return (1);
}
//function used to create data socket
int dataConnection(int connectfd){
					struct sockaddr_in dataservaddr,dataclientaddr;
					int rv, newPort, datamysock,datalength,dataconnectfd;
					char str[6];
					char *myrespond;
					datamysock = socket(AF_INET,SOCK_STREAM,0);
					if(datamysock < 0){
						myerror(connectfd,strerror(errno));
						return -1;
					}
					rv = setsockopt(datamysock,SOL_SOCKET,SO_REUSEADDR,&(int){1},sizeof(int));
					if(rv < 0){
						myerror(connectfd,strerror(errno));
						return -1;
					}
					dataservaddr.sin_family = AF_INET;
					dataservaddr.sin_addr.s_addr = htonl(INADDR_ANY);
					dataservaddr.sin_port = htons(0);	//0 is wildcard for any available port
					rv = bind(datamysock,(struct sockaddr*)&dataservaddr,sizeof(dataservaddr));
					if(rv < 0){
						myerror(connectfd,strerror(errno));
						return -1;
					}
					datalength = sizeof(struct sockaddr_in);
					rv = getsockname(datamysock,(struct sockaddr*)&dataservaddr,&datalength);
					if(rv < 0){
						myerror(connectfd,strerror(errno));
						return -1;
					}
					newPort=ntohs(dataservaddr.sin_port);
					if(debug) printf("Preparing response\n");
					sprintf(str, "%d", newPort);
					listen(datamysock,1);
					strcat(str,"\n");
					write(connectfd,"A",1);			//acknowledge, A<port>\n
					write(connectfd,str,6);
					if(debug) printf("Sent:%s!\n",str);
					dataconnectfd = accept(datamysock,(struct sockaddr*)&dataclientaddr,&datalength);
					if(debug) printf("Connection made!\n");
					if(dataconnectfd < 0){
						myerror(connectfd,strerror(errno));
						return -1;
					}
					else{
						return (dataconnectfd);
					}
	}

