package org.samsha.iec.ui;

import java.io.*;
import java.util.*;
import java.lang.String;

public class Apriori {

    public static void main(String[] args) {
        AprioriCalculation ap = new AprioriCalculation();

        ap.aprioriProcess();
    }
}
class AprioriCalculation
{
    Vector<String> candidates=new Vector<String>(); //the current candidates
    ArrayList<Vector<String>> backCand = new ArrayList<Vector<String>>();
    String configFile="config.txt"; //configuration file
    String transaFile="dataset.txt"; //transaction file
    String outputFile="apriori-output.txt";//output file
    int numItems; //number of items per transaction
    int numTransactions; //number of transactions
    double minSup; //minimum support for a frequent itemset
    String oneVal[]; //array of value per column that will be treated as a '1'
    String itemSep = " "; //the separator value for items in the database
    String product[]={"smooth & silky","Sensitive care","Refreshing Cool","Classic Clean","Sleek & Shine","Long and strong","Dry and Damaged","Oily Roots"};
	int cost[]={140,155,147,189,175,132,156,160};
	int Tcost[];
    public void aprioriProcess()
    {
        Vector<String> c=new Vector<String>(); //the current candidates
        Date d; //date object for timing purposes
        long start, end; //start and end time
        int itemsetNumber=0,i=0,k,a=0,count=0; //the current itemset being looked at
        //Integer j=new Integer(3);
        //String j;
        
        //get config
        getConfig();

        System.out.println("Apriori algorithm has started.\n");

        //start timer
        d = new Date();
        start = d.getTime();

        //while not complete
        do
        {
            //increase the itemset that is being looked at
            itemsetNumber++;

            //generate the candidates
            generateCandidates(itemsetNumber);

            //determine and display frequent itemsets
            calculateFrequentItemsets(itemsetNumber);
            if(candidates.size()!=0)
            {
                System.out.println("Frequent " + itemsetNumber + "-itemsets");
                System.out.println(candidates);
            }
        //if there are <=1 frequent items, then its the end. This prevents reading through the database again. When there is only one frequent itemset.
        }while(candidates.size()>1);

        //end timer
        d = new Date();
        end = d.getTime();
		c=candidates;
		String[] j= new String[c.size()];
				
        //display the execution time
        System.out.println("Execution time is: "+((double)(end-start)/1000) + " seconds.");
        for(String str:c)
        	j[i++]=str;
        	
       // ArrayList<Integer> costArray = new ArrayList<Integer>();
        int TotalCost = 0;	
        for(k=0;k<j.length;k++)
        	{
        		//a=Integer.valueOf(j[k]);
        		//a=(int)j[k];
        		String[] items = j[k].split(" ");
        		for (String item : items) 
        		{
        			a=Integer.valueOf(item);
        			//System.out.println(a);
        			System.out.println(product[a-1]);
        			TotalCost+=cost[a-1];
        			//costArray.add(product[a-1]);
        			count++;
        		}
        		/*int noOfItems = 0;
        		int TotalCost = 0;
        		for (int costOfItem : costArray) {
        			TotalCost += costOfItem;
        			noOfItems++;
        		}*/
        		
        		//System.out.println("Average Cost : " + TotalCost/noOfItems);
        		System.out.println("Average Cost : " + TotalCost/count);
        		System.out.println("The Above attributes should contain in the new product");
        		Tcost=new int[count];
        		//System.out.println(j[k]);
        		
			}
        
        
        //	System.out.println("hahahaqqqqq");
        /*{
        	j=candidates;
        	
        	System.out.println(j);
        }*/
    }

    public static String getInput()
    {
        String input="";
        //read from System.in
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        //try to get users input, if there is an error print the message
        try
        {
            input = reader.readLine();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        return input;
    }

    private void getConfig()
    {
        FileWriter fw;
        BufferedWriter file_out;

        String input="";

        try
        {
             FileInputStream file_in = new FileInputStream(configFile);
             BufferedReader data_in = new BufferedReader(new InputStreamReader(file_in));
             //number of items
             numItems=Integer.valueOf(data_in.readLine()).intValue();

             //number of transactions
             numTransactions=Integer.valueOf(data_in.readLine()).intValue();

             //minsup
             minSup=(Double.valueOf(data_in.readLine()).doubleValue());

             //output config info to the user
             System.out.print("\nInput configuration: "+numItems+" items, "+numTransactions+" transactions, ");
             System.out.println("minsup = "+minSup+"%");
             System.out.println();
             minSup/=100.0;

            oneVal = new String[numItems];
            System.out.print("Enter 'y' to change the value each row recognizes as a '1':");
            if(getInput().compareToIgnoreCase("y")==0)
            {
                for(int i=0; i<oneVal.length; i++)
                {
                    System.out.print("Enter value for column #" + (i+1) + ": ");
                    oneVal[i] = getInput();
                }
            }
            else
                for(int i=0; i<oneVal.length; i++)
                    oneVal[i]="1";

            //create the output file
            fw= new FileWriter(outputFile);
            file_out = new BufferedWriter(fw);
            //put the number of transactions into the output file
            file_out.write(numTransactions + "\n");
            file_out.write(numItems + "\n******\n");
            file_out.close();
        }
        //if there is an error, print the message
        catch(IOException e)
        {
            System.out.println(e);
        }
    }
    
    private void generateCandidates(int n)
    {
        Vector<String> tempCandidates = new Vector<String>(); //temporary candidate string vector
        String str1, str2; //strings that will be used for comparisons
        StringTokenizer st1, st2; //string tokenizers for the two itemsets being compared

        //if its the first set, candidates are just the numbers
        if(n==1)
        {
            for(int i=1; i<=numItems; i++)
            {
                tempCandidates.add(Integer.toString(i));
            }
        }
        else if(n==2) //second itemset is just all combinations of itemset 1
        {
            //add each itemset from the previous frequent itemsets together
            for(int i=0; i<candidates.size(); i++)
            {
                st1 = new StringTokenizer(candidates.get(i));
                str1 = st1.nextToken();
                for(int j=i+1; j<candidates.size(); j++)
                {
                    st2 = new StringTokenizer(candidates.elementAt(j));
                    str2 = st2.nextToken();
                    tempCandidates.add(str1 + " " + str2);
                }
            }
        }
        else
        {
            //for each itemset
            for(int i=0; i<candidates.size(); i++)
            {
                //compare to the next itemset
                for(int j=i+1; j<candidates.size(); j++)
                {
                    //create the strigns
                    str1 = new String();
                    str2 = new String();
                    //create the tokenizers
                    st1 = new StringTokenizer(candidates.get(i));
                    st2 = new StringTokenizer(candidates.get(j));

                    //make a string of the first n-2 tokens of the strings
                    for(int s=0; s<n-2; s++)
                    {
                        str1 = str1 + " " + st1.nextToken();
                        str2 = str2 + " " + st2.nextToken();
                    }

                    //if they have the same n-2 tokens, add them together
                    if(str2.compareToIgnoreCase(str1)==0)
                        tempCandidates.add((str1 + " " + st1.nextToken() + " " + st2.nextToken()).trim());
                }
            }
        }
        //clear the old candidates
        candidates.clear();
        //set the new ones
        candidates = new Vector<String>(tempCandidates);
        tempCandidates.clear();
        
    }

    private void calculateFrequentItemsets(int n)
    {
        Vector<String> frequentCandidates = new Vector<String>(); //the frequent candidates for the current itemset
        FileInputStream file_in; //file input stream
        BufferedReader data_in; //data input stream
        FileWriter fw;
        BufferedWriter file_out;

        StringTokenizer st, stFile; //tokenizer for candidate and transaction
        boolean match; //whether the transaction has all the items in an itemset
        boolean trans[] = new boolean[numItems]; //array to hold a transaction so that can be checked
        int count[] = new int[candidates.size()]; //the number of successful matches

        try
        {
                //output file
                fw= new FileWriter(outputFile, true);
                file_out = new BufferedWriter(fw);
                //load the transaction file
                file_in = new FileInputStream(transaFile);
                data_in = new BufferedReader(new InputStreamReader(file_in));

                //for each transaction
                for(int i=0; i<numTransactions; i++)
                {
                    //System.out.println("Got here " + i + " times"); //useful to debug files that you are unsure of the number of line
                    stFile = new StringTokenizer(data_in.readLine(), itemSep); //read a line from the file to the tokenizer
                    //put the contents of that line into the transaction array
                    for(int j=0; j<numItems; j++)
                    {
                        trans[j]=(stFile.nextToken().compareToIgnoreCase(oneVal[j])==0); //if it is not a 0, assign the value to true
                    }

                    //check each candidate
                    for(int c=0; c<candidates.size(); c++)
                    {
                        match = false; //reset match to false
                        //tokenize the candidate so that we know what items need to be present for a match
                        st = new StringTokenizer(candidates.get(c));
                        //check each item in the itemset to see if it is present in the transaction
                        while(st.hasMoreTokens())
                        {
                            match = (trans[Integer.valueOf(st.nextToken())-1]);
                            if(!match) //if it is not present in the transaction stop checking
                                break;
                        }
                        if(match) //if at this point it is a match, increase the count
                            count[c]++;
                    }

                }
                for(int i=0; i<candidates.size(); i++)
                {
                    //  System.out.println("Candidate: " + candidates.get(c) + " with count: " + count + " % is: " + (count/(double)numItems));
                    //if the count% is larger than the minSup%, add to the candidate to the frequent candidates
                    if((count[i]/(double)numTransactions)>=minSup)
                    {
                        frequentCandidates.add(candidates.get(i));
                        //put the frequent itemset into the output file
                        file_out.write(candidates.get(i) + "," + count[i]/(double)numTransactions + "\n");
                    }
                }
                file_out.write("-\n");
                file_out.close();
        }
        //if error at all in this process, catch it and print the error messate
        catch(IOException e)
        {
            System.out.println(e);
        }
        //clear old candidates
        this.backCand.add(new Vector<String>(candidates));
        candidates.clear();
        //new candidates are the old frequent candidates
        candidates = new Vector<String>(frequentCandidates);
        frequentCandidates.clear();
    }
}