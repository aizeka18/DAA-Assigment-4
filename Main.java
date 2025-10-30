package graph;

import graph.data.DatasetGenerator;
import java.io.File;


public class Main {
    public static void main(String[] args) {
        try {
            // Check if datasets exist, generate if missing
            File dataDir = new File("data");
            if (!dataDir.exists() || dataDir.list().length == 0) {
                System.out.println("📁 Generating test datasets...");
                new DatasetGenerator().generateAllDatasets();
            }

            GraphProcessor processor = new GraphProcessor();

            if (args.length > 0) {
                if ("generate".equals(args[0])) {
                    // Regenerate datasets
                    System.out.println("🔄 Regenerating test datasets...");
                    new DatasetGenerator().generateAllDatasets();
                } else {
                    // Process specific dataset
                    processor.processDataset(args[0]);
                }
            } else {
                // Process all datasets
                System.out.println("🚀 Starting Smart City Scheduling Analysis...");
                processor.processAllDatasets();
            }

        } catch (Exception e) {
            System.err.println("❌ Error in application: " + e.getMessage());
            e.printStackTrace();
        }
    }
}