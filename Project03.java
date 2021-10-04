
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Project03 {
	private static List<Individual> individualList = new ArrayList<Individual>();
	private static List<Family> familyList = new ArrayList<Family>();

	public static void printINDIAndFAMTables(File f) {
        BufferedReader br = null;

        try {
	    	br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
	    	
	    	boolean BirthDate = false;
	    	boolean DeathDate = false;
	    	boolean MarriageDate = false;
	    	boolean DivorceDate = false;
	    	Individual indv = null;
	    	Family curFamily = null;

	    	// Read each line from the file
	    	for (String line = br.readLine(); line != null; line = br.readLine()) {

				// Split the line into words	
				String[] words = line.split(" ");

				// Check if the line is valid
				if(words != null && words.length >= 2) {
					
					boolean isSpecial = false;
					if(words.length >= 3 && (words[2].equals("INDI") || words[2].equals("FAM")) ) {
						isSpecial = true;
					}
					
					// Obtain level, tag and value from the line
					String level = words[0];
					String tag = "";
					String value = "";
					
					if(!isSpecial) {
						tag = words[1];
						for(int i=2;i<words.length;i++) {
							value += words[i] + " ";
						}
					} else {
						tag = words[2];
						value = words[1];
						for(int i=3;i<words.length;i++) {
							value += " " +words[i];
						}
					}
					
					// Check if we encounter INDI record, 
					if(tag.equals("INDI")) {
						indv = new Individual();
						indv.setId(value);
						
						individualList.add(indv);
					}
					//Get value of NAME
					if(tag.equals("NAME") && indv != null) {
						indv.setName(value);
					}
					//Get value of SEX
					if(tag.equals("SEX")) {
						indv.setGender(value);
					}
					//Judge birth date
					if(tag.equals("BIRT")) {
						BirthDate = true;
						continue;
					}
					//Get value of birth date
					if(tag.equals("DATE") && BirthDate) {
						BirthDate = false;
						indv.setBirthDate(value);
					}
					//Judge marriage date
					if(tag.equals("MARR")) {
						MarriageDate = true;
						continue;
					}
					//Get value of marriage date
					if(tag.equals("DATE") && MarriageDate) {
						MarriageDate = false;
						curFamily.setMarriageDate(value);
					}
					//Judge death
					if(tag.equals("DEAT")) {
						DeathDate = true;
						continue;
					}
					//Get value of death date
					if(tag.equals("DATE") && DeathDate) {
						DeathDate = false;
						indv.setDeathDate(value);
					}
					//Judge divorce
					if(tag.equals("DIV")) {
						DivorceDate = true;
						continue;
					}
					//Get value of divorce date
					if(tag.equals("DATE") && DivorceDate) {
						DivorceDate = false;
						curFamily.setDivorceDate(value);
					}
					//Get value of family information
					if(tag.equals("FAM")) {
						curFamily = null;
						for (Family family:familyList) {
							if(family.getId().trim().equals((value.replaceAll("@", "")).trim()))
								curFamily = family;
						}
						if(curFamily == null) {
							curFamily = new Family();
							curFamily.setId(value.replaceAll("@", ""));
							familyList.add(curFamily);
						}
					}
					//Get value of spouse family information
					if(tag.equals("FAMS")) {
						indv.getSpouseFamilyIds().add(value);
						
						// Add the spouse to family table
						Family fam = null;
						for (Family family:familyList) {
							if(family.getId().equals(value.replaceAll("@", "")))
								fam = family;
						}
						if(fam == null) {
							fam = new Family();
							fam.setId(value.replaceAll("@", ""));
							familyList.add(fam);
						}
						if(indv.getGender().trim().equals("M")) {
							fam.setHusbandName(indv.getName());
							fam.setHusbandId(indv.getId());
						} else {
							fam.setWifeName(indv.getName());
							fam.setWifeId(indv.getId());
						}
					}
					//Get value of children family information
					if(tag.equals("FAMC")) {
						indv.getChildFamilyIds().add(value);
						
						//Add the children IDs to the family table
						Family fam = null;
						for (Family family:familyList) {
							if(family.getId().equals(value.replaceAll("@", "")))
								fam = family;
						}
						if(fam == null) {
							fam = new Family();
							fam.setId(value.replaceAll("@", ""));
							familyList.add(fam);
						}
						if(fam.getChildrenId() == null) {
						List<String> childrenIds = new ArrayList<String>();
						childrenIds.add("'"+indv.getId()+"'");
						fam.setCId(childrenIds);
						}
						else {
						fam.getChildrenId().add("'"+indv.getId()+"'");
						}
					}
					
				}
	    	}
	  
	    	System.out.println("Individuals");

	    	System.out.format("%1$-10s %2$-25s %3$-7s %4$-12s %5$-5s %6$-7s %7$-12s %8$-20s %9$-20s \n", 
	    			          "ID", "Name", "Gender", "Birthday", "Age", "Alive", "Death", "Child", "Spouse");

	    	for(int i=0;i<individualList.size();i++) {
	    		Individual curIndv = individualList.get(i);
	    		System.out.format("%1$-10s %2$-25s %3$-7s %4$-12s %5$-5s %6$-7s %7$-12s %8$-20s %9$-20s \n", 
	    						  curIndv.getId(), curIndv.getName(), curIndv.getGender(),
	    			        	  curIndv.getBirthDate(), curIndv.getAge(), curIndv.isAlive(),
	    				          curIndv.getDeathDate(), curIndv.getChildFIdsAsStr(), 
	    				          curIndv.getSpouseFIdsAsStr());
	    	}
	    	//Print all families in list familyList
	    	System.out.println("Families");
	    	System.out.format("%1$-10s %2$-12s %3$-12s %4$-5s %5$-25s %6$-10s %7$-25s %8$-20s \n", 
	    						"ID", "Married", "Divorced", "Husband ID", "Husband Name", "Wife ID", "Wife Name", "Children");

	    	for(Family fam: familyList)
	    		System.out.format("%1$-10s %2$-12s %3$-12s %4$-10s %5$-25s %6$-10s %7$-25s %8$-20s \n", 
	    		                  fam.getId(), fam.getMarriageDate() == null ? "NA" : fam.getMarriageDate(), 
	    		                  fam.getDivorceDate() == null ? "NA" : fam.getDivorceDate(), fam.getHusbandId(),
	    		                  fam.getHusbandName(),fam.getWifeId(), fam.getWifeName(),fam.getCIdAsStr());	    							
        }  catch (IOException e) {
        	//e.printStackTrace();
        	System.out.println("Please enter correct file name");
        }  finally {
        	try {
        		if (br != null)br.close();
        		
        	} catch (IOException ex) {
        		ex.printStackTrace();
        	}
        }

	}
	public static List<Family> getFamilies(){
		return familyList;
	}
	
	public static List<Individual> getIndividuals(){
		return individualList;
	}
	
	public static void main(String[] args) throws IOException {
		Project03 p = new Project03();
//You can change local path here
		File file = new File("C:\\Users\\Left丶\\OneDrive - stevens.edu\\桌面\\555\\SSW555WSGroup5.ged");
		p.printINDIAndFAMTables(file);
	}
}
