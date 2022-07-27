import java.util.*;
import java.io.*;

public class RunAtLeastKCorrelation {


    private static ArrayList<ArrayList<Integer>> addExtraClusters(ArrayList<ArrayList<Integer>> cur_clustering, int k) {
        // postprocess to enforce cluster limit

        int cur_index = cur_clustering.size() - 1;

        while (cur_clustering.size() < k) {
            if (cur_clustering.get(cur_index).size() <= 1) 
                cur_index--;
            ArrayList<Integer> new_cluster = new ArrayList<Integer>();
            Integer my_item = cur_clustering.get(cur_index).get(cur_clustering.get(cur_index).size() - 1);
            new_cluster.add(my_item);
            cur_clustering.get(cur_index).remove(cur_clustering.get(cur_index).size() - 1);
            cur_clustering.add(new_cluster);
        }

        return cur_clustering;

    }


    public static void main(String args[]){

        String data_set = args[0];
        String delimiter = "\\s"; 

        ArrayList<ArrayList<Integer>> prob_matrix = Helper.read_large_network_relabel("Data/"+ data_set + "/graph.txt", delimiter);

        int ROUNDS = 10;

        ArrayList<Integer> test_vals = new ArrayList<Integer>();
        for (int i = 140000; i < 300001; i += 10000)
            test_vals.add(i);

        Object[] k_vals = test_vals.toArray(); // {600000, 650000, 700000, 750000, 800000, 850000, 900000, 950000, 1000000, 1050000, 1100000, 1150000, 1200000, 1250000, 1300000, 1350000, 1400000, 1450000, 1500000};

        for (int q = 0; q < k_vals.length; q++) {

        // Collect numbers here
        double[] pivotTimes = new double[ROUNDS];
        long[] pivotScores = new long[ROUNDS];

        double[] fillTimes = new double[ROUNDS];
        long[] fillScores = new long[ROUNDS];

        double[] randTimes = new double[ROUNDS];
        long[] randScores = new long[ROUNDS];

        double[] blendTimes = new double[ROUNDS];
        long[] blendScores = new long[ROUNDS];

        double[] hybridTimes = new double[ROUNDS];
        long[] hybridScores = new long[ROUNDS];


        double pivotTimeTotal = 0;
        double fillTimeTotal = 0;
        double randTimeTotal = 0;
        double blendTimeTotal = 0;
        double hybridTimeTotal = 0;
  
        int pivotNumClusters = 0;
        int fillNumClusters = 0;
        int randNumClusters = 0;
        int blendNumClusters = 0;
        int hybridNumClusters = 0;

        int largestPivotCluster = 0;
        int largestFillCluster = 0;
        int largestRandCluster = 0;
        int largestBlendCluster = 0;
        int largestHybridCluster = 0;

        long pivotScoreTotal = 0;
        long fillScoreTotal = 0;
        long randScoreTotal = 0;
	long blendScoreTotal = 0;
	long hybridScoreTotal = 0;

        int k = (int) k_vals[q]; 

        System.out.println("Start: k = " + k);
        for (int j = 0; j < ROUNDS; j++) {

	System.out.println("Start pivot...");
        long pivotStart = System.currentTimeMillis();
        ArrayList<ArrayList<Integer>> pivot_result = PKwik.exactly_k_pivot(prob_matrix, k);
        long pivotTime = System.currentTimeMillis() - pivotStart;
        pivotTimeTotal += (pivotTime / 1000.0);
        pivotTimes[j] = pivotTime;

        long fillStart = System.currentTimeMillis();
        ArrayList<ArrayList<Integer>> fill_result = pivot_result;
        long fillTime = System.currentTimeMillis() - fillStart;
        fillTimeTotal += (fillTime / 1000.0);
        fillTimes[j] = fillTime;

	System.out.println("Start Node");
        long randStart = System.currentTimeMillis();
        ArrayList<ArrayList<Integer>> rand_result = DNode.exactly_k_random_node_network(prob_matrix, k); 
        long randTime = System.currentTimeMillis() - randStart;
        randTimeTotal += (randTime / 1000.0);
        randTimes[j] = randTime;

        long blendStart = System.currentTimeMillis();
        ArrayList<ArrayList<Integer>> blend_result = pivot_result;
        long blendTime = System.currentTimeMillis() - blendStart;
        blendTimeTotal += (blendTime / 1000.0); 
        blendTimes[j] = blendTime;

        long hybridStart = System.currentTimeMillis();
        ArrayList<ArrayList<Integer>> fix = pivot_result;
        long hybridTime = System.currentTimeMillis() - hybridStart;
        hybridTimeTotal += (hybridTime / 1000.0);
        hybridTimes[j] = hybridTime;

        // ADJUST
        // pivot_result = addExtraClusters(pivot_result, k);
        // rand_result = addExtraClusters(rand_result, k);

        pivotNumClusters += pivot_result.size();
        fillNumClusters += fill_result.size();
        randNumClusters += rand_result.size();
        blendNumClusters += blend_result.size();
        hybridNumClusters += fix.size();

        int max_pivot = 0;
        for (int i = 0; i < pivot_result.size(); i++) {
            if (pivot_result.get(i).size() > max_pivot)
                max_pivot = pivot_result.get(i).size();
        }
        largestPivotCluster += max_pivot;

        int max_fill = 0;
        for (int i = 0; i < fill_result.size(); i++) {
            if (fill_result.get(i).size() > max_fill)
                max_fill = fill_result.get(i).size();
        }
        largestFillCluster += max_fill;

        int max_rand = 0;
        for (int i = 0; i < rand_result.size(); i++) {
            if (rand_result.get(i).size() > max_rand)
                max_rand = rand_result.get(i).size();
        }
        largestRandCluster += max_rand;

        int max_blend = 0;
        for (int i = 0; i < blend_result.size(); i++) {
            if (blend_result.get(i).size() > max_blend)
                max_blend = blend_result.get(i).size();
        }
        largestBlendCluster += max_blend;
        
        int max_hybrid = 0;
        for (int i = 0; i < fix.size(); i++) {
            if (fix.get(i).size() > max_hybrid)
                max_hybrid = fix.get(i).size();
        }
        largestHybridCluster += max_hybrid;
        

        long pivot_score = Helper.quick_edit_dist(pivot_result, prob_matrix);
        pivotScoreTotal += pivot_score;
        pivotScores[j] = pivot_score;

        long fill_score = Helper.quick_edit_dist(fill_result, prob_matrix);
        fillScoreTotal += fill_score;
        fillScores[j] = fill_score;

        long rand_score = Helper.quick_edit_dist(rand_result, prob_matrix);
        randScoreTotal += rand_score;
        randScores[j] = rand_score;

        long blend_score = Helper.quick_edit_dist(blend_result, prob_matrix);
        blendScoreTotal += blend_score;
        blendScores[j] = blend_score;

        long hybrid_score = Helper.quick_edit_dist(fix, prob_matrix); 
        hybridScoreTotal += hybrid_score;
        hybridScores[j] = hybrid_score;

        }

        System.out.println("Finish");
        System.out.println();

        System.out.println("Pivot times: ");
        for (int i = 0; i < ROUNDS; i++)
            System.out.print(pivotTimes[i] + " ");
        System.out.println();
        System.out.println();
        System.out.println("Pivot scores: ");
        for (int i = 0; i < ROUNDS; i++)
            System.out.print(pivotScores[i] + " ");
        System.out.println();
        System.out.println();
        /*
        System.out.println("Fill times: ");
        for (int i = 0; i < ROUNDS; i++)
            System.out.print(fillTimes[i] + " ");
        System.out.println();
        System.out.println();
        System.out.println("Fill scores: ");
        for (int i = 0; i < ROUNDS; i++)
            System.out.print(fillScores[i] + " ");
        System.out.println();
        System.out.println();
        */
        System.out.println("Rand times: ");
        for (int i = 0; i < ROUNDS; i++)
            System.out.print(randTimes[i] + " ");
        System.out.println();
        System.out.println();
        System.out.println("Rand scores: ");
        for (int i = 0; i < ROUNDS; i++)
            System.out.print(randScores[i] + " ");
        System.out.println();
        System.out.println();
        /*
        System.out.println("Blend times: ");
        for (int i = 0; i < ROUNDS; i++)
            System.out.print(blendTimes[i] + " ");
        System.out.println();
        System.out.println();
        System.out.println("Blend scores: ");
        for (int i = 0; i < ROUNDS; i++)
            System.out.print(blendScores[i] + " ");
        System.out.println();
        System.out.println();
        System.out.println("RNode times: ");
        for (int i = 0; i < ROUNDS; i++)
            System.out.print(hybridTimes[i] + " ");
        System.out.println();
        System.out.println();
        System.out.println("RNode scores: ");
        for (int i = 0; i < ROUNDS; i++)
            System.out.print(hybridScores[i] + " ");
        System.out.println();
        System.out.println();
        */

        System.out.println("Average k Pivot time: " + pivotTimeTotal / ((double) ROUNDS));
        //System.out.println("Average k Fill time: " + fillTimeTotal / ((double) ROUNDS));
        System.out.println("Average k Rand time: " + randTimeTotal / ((double) ROUNDS));
        //System.out.println("Average k Blend time: " + blendTimeTotal / ((double) ROUNDS));
        //System.out.println("Average k RNode time: " + hybridTimeTotal / ((double) ROUNDS));
        System.out.println();
        System.out.println("Average k Pivot score: " + pivotScoreTotal / ((double) ROUNDS));
        //System.out.println("Average k Fill score: " + fillScoreTotal / ((double) ROUNDS));
        System.out.println("Average k Rand score: " + randScoreTotal / ((double) ROUNDS));
	//System.out.println("Average k Blend score: " + blendScoreTotal / ((double) ROUNDS));
	//System.out.println("Average k RNode score: " + hybridScoreTotal / ((double) ROUNDS));
        System.out.println();
        System.out.println("Average k Pivot num clusters: " + pivotNumClusters / ((double) ROUNDS));
        //System.out.println("Average k Fill num clusters: " + fillNumClusters / ((double) ROUNDS));
        System.out.println("Average k Rand num clusters: " + randNumClusters / ((double) ROUNDS));
        //System.out.println("Average k Blend num clusters: " + blendNumClusters / ((double) ROUNDS));
        //System.out.println("Average k RNode num clusters: " + hybridNumClusters / ((double) ROUNDS));
        System.out.println();
        System.out.println("Average k Pivot max cluster size: " + largestPivotCluster / ((double) ROUNDS));
        //System.out.println("Average k Fill max cluster size: " + largestFillCluster / ((double) ROUNDS));
        System.out.println("Average k Rand max cluster size: " + largestRandCluster / ((double) ROUNDS));
        //System.out.println("Average k Blend max cluster size: " + largestBlendCluster / ((double) ROUNDS));
        //System.out.println("Average k RNode max cluster size: " + largestHybridCluster / ((double) ROUNDS));     
        System.out.println();


        }


    }
    
}
