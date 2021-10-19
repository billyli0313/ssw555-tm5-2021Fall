import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Project03 {
	private static List<Individual> individualList = new ArrayList<Individual>();
	private static List<Family> familyList = new ArrayList<Family>();
	private static List<String> indiList = new ArrayList<String>();
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
						if(indiList.contains(value)){
							System.out.println("INDI "+value+" already exsit");
						}else{
							individualList.add(indv);
							indiList.add(value);
						}

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

	    	for(Family fam: familyList) {
	    		System.out.format("%1$-10s %2$-12s %3$-12s %4$-10s %5$-25s %6$-10s %7$-25s %8$-20s \n", 
	    		                  fam.getId(), fam.getMarriageDate() == null ? "NA" : fam.getMarriageDate(), 
	    		                  fam.getDivorceDate() == null ? "NA" : fam.getDivorceDate(), fam.getHusbandId(),
	    		                  fam.getHusbandName(),fam.getWifeId(), fam.getWifeName(),fam.getCIdAsStr());	  
	    	}
	    	//Sprint1:US01:Dates before current date
	    	//shift name of month to number
			Map<String,Integer> tags = new HashMap<String,Integer>(); 
			tags.put("JAN",1); 
			tags.put("FEB",2); 
			tags.put("MAR",3); 
			tags.put("APR",4); 
			tags.put("MAY",5); 
			tags.put("JUN",6); 
			tags.put("JUL",7); 
			tags.put("AUG",8); 
			tags.put("SEP",9); 
			tags.put("OCT",10); 
			tags.put("NOV",11); 
			tags.put("DEC",12); 
			//get current date
	    	Date date = new Date();
	    	int curyear = Integer.parseInt(String.format("%tY", date));
	    	int curmonth = Integer.parseInt(String.format("%tm", date));
	    	int curday = Integer.parseInt(String.format("%te", date));
	    	// variable of birthday
	    	int count = 0;
	    	int day = 0;
	    	int month = 0;
	    	int year = 0;
	    	//variable of death day
	    	int deathcount = 0;
	    	int deathday = 0;
	    	int deathmonth = 0;
	    	int deathyear = 0;
	    	//variable of marriage day
	    	int marcount = 0;
	    	int marday = 0;
	    	int marmonth = 0;
	    	int maryear = 0;
	    	//variable of divorce day
	    	int divcount = 0;
	    	int divday = 0;
	    	int divmonth = 0;
	    	int divyear = 0;  	
	    	    		
		    		//US03 Birthday before Deathday
	    		for(int i=0;i<individualList.size();i++) {
		    		Individual curIndv = individualList.get(i);
		    		
		    		count = 0; 
		    		for(String member:curIndv.getBirthDate().split(" ")) {
		    			count++;
		    			if(count==1) {
		    				day = Integer.parseInt(member);
		    			}else if(count==2){
		    				month = tags.get(member);	
		    			}else if(count==3){
		    				year = Integer.parseInt(member);	
		    				
		    				deathcount = 0;
			    				if(!curIndv.getDeathDate().equals("NA")) 
		    			for(String member1:curIndv.getDeathDate().split(" ")) {
		    					deathcount++;
		    	    			if(deathcount==1) {
		    	    				deathday = Integer.parseInt(member1);
		    	    			}else if(deathcount==2){
		    	    				deathmonth = tags.get(member1);	
		    	    			}else if(deathcount==3){
		    	    				deathyear = Integer.parseInt(member1);}}
		    			
		    			if(!curIndv.getDeathDate().equals("NA")) 
		    				if(year>deathyear) {
		    					System.out.println("ERROR: INDIVIDUAL: US03: " +curIndv.getId()+": Birthday  "+ curIndv.getBirthDate() +"after Deathday"+curIndv.getDeathDate());
		    					
		    				}
		    				else if(year==deathyear && month>deathmonth) {
		    					System.out.println("ERROR: INDIVIDUAL: US03: " +curIndv.getId()+": Birthday  "+ curIndv.getBirthDate() +"after Deathday"+curIndv.getDeathDate());
		    					
		    				}
		    				else if(year==deathyear && month==deathmonth && day>deathday) {
		    					System.out.println("ERROR: INDIVIDUAL: US03: " +curIndv.getId()+": Birthday "+ curIndv.getBirthDate() +"after Deathday"+curIndv.getDeathDate());
		    					
		    				}
		    				deathcount = 0;
		    				count = 0;
		    			}
		    	    			
			    				
		    			
	    				
	    		}
		    		
	    		
	    		}
	    		
	    		
	    		
	  
	    		//Sprint1:US05:  marriage before death
	    		for(Family fam: familyList) {
	    		if(!fam.getMarriageDate().equals("NA")) {
	    		marcount=0;
	    		for(String member:fam.getMarriageDate().split(" ")) {
	    			marcount++;
	    			if(marcount==1) {
	    				marday = Integer.parseInt(member);
	    			}else if(marcount==2){
	    				marmonth = tags.get(member);	
	    			}else if(marcount==3){
	    				maryear = Integer.parseInt(member);	
	    				marcount=0;
	    			}
	    		}
	    		
	    		count=0;
	    		for(int i=0;i<individualList.size();i++) {
	    			Individual curIndv = individualList.get(i);
	    			
	    			if(curIndv.getId().equals(fam.getHusbandId())) {
	    				if(!curIndv.getDeathDate().equals("NA")) {
	    				for(String member:curIndv.getDeathDate().split(" ")) {
	    	    			count++;
	    	    			if(count==1) {
	    	    				day =Integer.parseInt(member);
	    	    			}else if(count==2){
	    	    				month = tags.get(member);	
	    	    			}else if(count==3){
	    	    				year = Integer.parseInt(member);	
	    	    				if(year>maryear) {
	    	    					System.out.println("ERROR: FAMILY: US05: " +fam.getId()+" :Marriage date " + fam.getMarriageDate()+"before husband's DeathDate "+ curIndv.getDeathDate() );
	    	    				}
	    	    				if(year==maryear && month>marmonth) {
	    	    					System.out.println("ERROR: FAMILY: US05: " +fam.getId()+" :Marriage date " + fam.getMarriageDate()+"before husband's DeathDate "+ curIndv.getDeathDate() );
	    	    				}
	    	    				if(year==maryear && month==marmonth && day>marday) {
	    	    					System.out.println("ERROR: FAMILY: US05: " +fam.getId()+" :Marriage date " + fam.getMarriageDate()+"before husband's DeathDate "+ curIndv.getDeathDate() );
	    	    				}
	    	    				count = 0;
	    	    			}
	    				}
	    			  }
	    			}
	    		
	    			if(curIndv.getId().equals(fam.getWifeId())) {
	    				if(!curIndv.getDeathDate().equals("NA")) {
	    				for(String member:curIndv.getDeathDate().split(" ")) {
	    	    			count++;
	    	    			if(count==1) {
	    	    				day = Integer.parseInt(member);
	    	    			}else if(count==2){
	    	    				month = tags.get(member);	
	    	    			}else if(count==3){
	    	    				year = Integer.parseInt(member);	
	    	    				if(maryear>year) {
	    	    					System.out.println("ERROR: FAMILY: US05: " +fam.getId()+" :Marriage date " + fam.getMarriageDate()+"before wife's DeathDate "+ curIndv.getDeathDate() );
	    	    				}
	    	    				if(maryear==year && marmonth>month) {
	    	    					System.out.println("ERROR: FAMILY: US05: " +fam.getId()+ ":Marriage date " + fam.getMarriageDate()+" wife's DeathDate "+ curIndv.getDeathDate());
	    	    				}
	    	    				if(maryear==year && marmonth==month && marday>day) {
	    	    					System.out.println("ERROR: FAMILY: US05: " +fam.getId()+" :Marriage date " + fam.getMarriageDate()+"before wife's DeathDate "+ curIndv.getDeathDate() );
	    	    				}
	    	    				count = 0;
	    	    			}
	    				}
	    			  }
	    			}
	    	    }	
	    	  }
	       }
	    	
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
		File file = new File("D:\\CS SIT\\CS555\\group userstory\\ssw555-tm5-2021Fall test.ged");
		p.printINDIAndFAMTables(file);
	}
}