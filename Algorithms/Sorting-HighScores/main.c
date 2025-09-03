#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
/* CSE450 HW2
 * Jake Onkka
 * This program uses one command line argument and reads a list of players from stdinput
 * The only possible command line arguments are 'custom' or 'standard'
 * Standard runs the standard qsort on the given list
 * Custom runs my custom sort algorithm on the list, it is a mix of count sort and hoares
 *
 */


//standard qsort
int cmpfunc(const void *a, const void *b){
	return (*(int*)a - *(int*)b);
}
double Time(int arr[]){
	clock_t start = clock();
	qsort(arr,1000000,sizeof(int),cmpfunc);
	clock_t end = clock();
	return ((double)(end - start)) / CLOCKS_PER_SEC;
}

//counting sort counts how many times we've seen a certain value
//then with count, we add that value n times to our array
void countingSort(int arr[], int smallMax){
	int *count = (int*)malloc((smallMax+1)*sizeof(int));	//need to keep track of every number up to the max value
	for(int i = 0; i <= smallMax; i++){
		count[arr[i]]++;	//increase count where index = value
	}
	int *dest = arr;	//write straight back into our main array because it is already malloc'd
	for(int value = 0; value < smallMax; value++){
		while(count[value] > 0){
			count[value]--;
			*dest = value;
			dest++;
		}
	
	}
}
//pointers start on either side of the array
//left increment until value is too big
//right decrement until value is too small
//when both happen, we swap
int hoarePartition(int arr[], int lo, int hi){
	int pivot = arr[(hi+lo) / 2];
	int left = lo - 1;
	int right = hi + 1;

	while(1){
		left += 1;
		while(arr[left] < pivot){
			left += 1;
		}
		right -= 1;
		while(arr[right] > pivot){
			right -= 1;
		}
		if(left >= right){
			return right;
		}
		int temp = arr[left];
		arr[left] = arr[right];
		arr[right] = temp;
	}
}
//recursively partition around the pivot
void hoareQuicksort(int arr[], int lo, int hi){
	if(lo < hi){
		int pivotIndex = hoarePartition(arr,lo,hi);
		hoareQuicksort(arr,lo,pivotIndex);
		hoareQuicksort(arr,pivotIndex +1, hi);
	}

}
//custom sort uses primarily counting sort for numbers < 10000 which is roughly 98% of the dataset
//the other 2% is very high numbers, this favors hoare's algorithm
void customSort(int arr[]){
	int smallIndex = 0;
	int largeIndex = 0;
	int smallSize = 0;
//	printf("Custom Sort\n");
	for(int i = 0; i < 1000000; i++){
		if(arr[i] < 10000){
			smallSize++;
		}

	}
//	printf("Smallsize = %d\n",smallSize);
	int *small = malloc(smallSize*sizeof(int));
	int *large = malloc((1000001-smallSize)*sizeof(int));	//split data into two arrays, one for count sort, one for hoares
//	printf("Splitting Data\n");
	for(int i = 0; i < 1000000; i++){
		//printf("%d %d\n",i,arr[i]);
		if(arr[i] < 10000){
			small[smallIndex++] = arr[i];
		} else {
			large[largeIndex++] = arr[i];
		}
		
	}
//	printf("Counting Sort\n");
	countingSort(small, smallSize);
//	printf("Hoare Quicksort\n");
	hoareQuicksort(large,0,largeIndex-1);


	int currentIndex = 0;
	for(int i = 0; i < smallSize; i++){
		arr[currentIndex++] = small[i];	//rewrite the count sort array into our already allocated array
	}
	for(int i = 0; i < largeIndex; i++){
		arr[currentIndex++] = large[i];	//rewrite the hoare sort array into our already allocated array, BEGIN at the index immediately after count sort, this effectively combines both sorted arrays
	}


}
double customTime(int arr[]){
	clock_t start = clock();
	customSort(arr);
	clock_t end = clock();
	return ((double)(end - start)) / CLOCKS_PER_SEC;
}


//for fairness, we must read the player list first without accounting for custom or standard, so they both read the same way
int main(int argc, char *argv[]){
	_Bool isStandard = 0;		//bool, false by default, meaning custom, user must still declare custom in command line

	if(argc == 1){ printf("No\n"); return -1;}
	if(strcmp(argv[1],"custom") == 0){
		//	printf("Custom\n");
	}
	else if(strcmp(argv[1],"standard") == 0){
		isStandard = 1;
	//	printf("Standard\n");
	}
	else{ printf("Incorrect\n");return -1;}

	char input[256];
	int *breakdancing = malloc(1000000*sizeof(int));
	int *apiculture = malloc(1000000*sizeof(int));
	int *basket = malloc(1000000*sizeof(int));
	int *xbasket = malloc(1000000*sizeof(int));
	int *sword = malloc(1000000*sizeof(int));
	int *totalxp = malloc(1000000*sizeof(int));
	//printf("Begin Reading\n");

	for(int row = 0; row < 1000000; row++){
		fgets(input,1000,stdin);
		sscanf(input,"%d%d%d%d%d",&breakdancing[row],&apiculture[row],&basket[row],&xbasket[row],&sword[row]);
		totalxp[row] = breakdancing[row] + apiculture[row] + basket[row] + xbasket[row] + sword[row];
	}

	//printf("Done Reading\n");
	double time1, time2, time3, time4, time5, time6;


	//now that everything is read in and memory is allocated, we can diverge into whatever sorting method to see which one is really faster
	
	//Standard qsort
	if(isStandard){		//run each array on qsort and capture the time it took
	//	printf("Standard True\n");
		time1 = Time(breakdancing);	
		time2 = Time(apiculture);
		time3 = Time(basket);
		time4 = Time(xbasket);
		time5 = Time(sword);
		time6 = Time(totalxp);
	}


	//custom sort, count sort and hoares sort
	else if(!isStandard){	//run each array on custom sort and capture the time it took
	//	printf("Custom True\n");
		time1 = customTime(breakdancing);
		time2 = customTime(apiculture);
		time3 = customTime(basket);
		time4 = customTime(xbasket);
		time5 = customTime(sword);
		time6 = customTime(totalxp);
	}

	//the sorting and timing is done for each one, now we print them with the same exact code so there's no difference

	//after sorting and time calculations done, print the lists in reverse order
	printf("SKILL_BREAKDANCING\n");
	for(int i = (1000000-1); i >= 0; i--){
		printf("%d\n",breakdancing[i]);
	}
	printf("time taken %f\n\n",time1);


	printf("SKILL_APICULTURE\n");
	for(int i = (1000000-1); i >= 0; i--){
		printf("%d\n",apiculture[i]);
	}
	printf("time taken %f\n\n",time2);
	
	
	printf("SKILL_BASKET\n");
	for(int i = (1000000-1); i >= 0; i--){
		printf("%d\n",basket[i]);
	}
	printf("time taken %f\n\n",time3);
	
	
	printf("SKILL_XBASKET\n");
	for(int i = (1000000-1); i >= 0; i--){
		printf("%d\n",xbasket[i]);
	}
	printf("time taken %f\n\n",time4);
	
	
	printf("SKILL_SWORD\n");
	for(int i = (1000000-1); i >= 0; i--){
		printf("%d\n",sword[i]);
	}
	printf("time taken %f\n\n",time5);
	
	
	printf("TOTAL_XP\n");
	for(int i = (1000000-1); i >= 0; i--){
		printf("%d\n",totalxp[i]);
	}
	printf("time taken %f\n\n",time6);
	printf("total time taken %f\n",time1+time2+time3+time4+time5+time6);

	return 0;
}
