package sg.edu.nus.iss.assessment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class Main 
{
    private static final String msg = "Usage : java -cp <jar> sg.edu.nus.iss.assessment.App <csvfile> <templatefile>";
    public static void main( String[] args )
    {
        try{
            String csvData = args[0];
            String templateFile = args[1];
            if(csvData != null && templateFile != null){
                List<Mailmerge> result = load(csvData);
                searchAndReplace(result, templateFile);
            }
        }catch(ArrayIndexOutOfBoundsException e){
            System.out.println(msg);
        }
    }

    private static void searchAndReplace(List<Mailmerge> merges, 
            String templateFile){

        try {
            String templateContent = readMailMergeText(templateFile);
            System.out.println(templateContent);
            for(Mailmerge m : merges){
                String fn = m.getFirstName();
                String addr= m.getAddress().replace("\\n", "\n");;
                int yrs = m.getYears();
                String newTemplate  = templateContent;
                newTemplate = newTemplate.replace("__address__", addr+"\n\n");
                newTemplate = newTemplate.replace("__first_name__", fn);
                newTemplate = newTemplate.replace(",", ",\n");
                newTemplate = newTemplate.replace("__years__", String.valueOf(yrs));
                
                System.out.println(newTemplate);
                System.out.println("------------LINE BREAK-------------");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    private static synchronized String readMailMergeText(String mailmergeFile) 
        throws IOException{
        Path path = Paths.get(mailmergeFile);
        List<String> allLines = Files
                            .readAllLines(path, StandardCharsets.UTF_8);
        StringBuilder strBuilder = new StringBuilder();
        for(String line : allLines){
            strBuilder.append(line);
        }
        return strBuilder.toString();
    }

    private static List<Mailmerge> load(String csvData){
        List<Mailmerge> mailmerges = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(
                    new FileReader(csvData))) {
            // skip the first line which is the column names
            String line = br.readLine();
            while((line = br.readLine()) != null){
                String[] fields = line.split(",");
                Mailmerge m = 
                        new Mailmerge(
                                fields[0], 
                                fields[1], 
                                fields[2], 
                                Integer.parseInt(fields[3]));
                mailmerges.add(m);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return mailmerges;
    }
}
