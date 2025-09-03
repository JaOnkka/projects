//header file with declarations common to both files
#include<stdio.h>
#include<string.h>
#include<sys/types.h>
#include<sys/socket.h>
#include<sys/un.h>
#include<netdb.h>
#include<unistd.h>
#include<stdlib.h>
#include<errno.h>
#include<sys/wait.h>
#include<fcntl.h>
#include<sys/stat.h>
//Macros
#define myport 14095		//port #
#define bufsize 100		//how much to read from server
#define pathsize 100		//max size of pathname to write to server
