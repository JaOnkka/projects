all: myftp myftpserve
myftp: myftp.o 
	gcc -o myftp myftp.o
myftp.o: myftp.c myftp.h
	gcc -c myftp.c
myftpserve: myftpserve.o
	gcc -o myftpserve myftpserve.o
myftpserve.o: myftpserve.c myftp.h
	gcc -c myftpserve.c
clean:
	rm -f myftp myftpserve myftp.o myftpserve.o
