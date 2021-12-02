import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
public class Project03 {
	private static List<Individual> individualList = new ArrayList<Individual>();
	private static List<Family> familyList = new ArrayList<Family>();
	private static List<String> individualIDList = new ArrayList<String>();
	private static List<String> familyIDList = new ArrayList<String>();
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
						childrenIds.add(""+indv.getId()+"");
						fam.setCId(childrenIds);
						}
						else {
						fam.getChildrenId().add(""+indv.getId()+"");
						}
					}
					
				}
	    	}
			// Reorder family members by decreasing age
			Map<String,Integer> age_list = new HashMap<String,Integer>();
			for(int i =0;i<individualList.size();i++){
				age_list.put(individualList.get(i).getAge(),i);
			}
			TreeMap<String, Integer> sorted_age_list = new TreeMap<>(Comparator.reverseOrder());
 
			// Copy all data from hashMap into TreeMap
			sorted_age_list.putAll(age_list); 

			// sorted_age_list = sorted_age_list.descendingMap();
			String key = null;
			Integer index = null;
			Iterator iter = sorted_age_list.keySet().iterator();

	    	System.out.println("Individuals");
	    	System.out.format("%1$-10s %2$-25s %3$-7s %4$-12s %5$-5s %6$-7s %7$-12s %8$-20s %9$-20s \n", 
	    			          "ID", "Name", "Gender", "Birthday", "Age", "Alive", "Death", "Child", "Spouse");
			while (iter.hasNext()) {
				key = (String)iter.next();
				index = (Integer)sorted_age_list.get(key);
	    		Individual curIndv = individualList.get(index);
	    		System.out.format("%1$-10s %2$-25s %3$-7s %4$-12s %5$-5s %6$-7s %7$-12s %8$-20s %9$-20s \n", 
	    						  curIndv.getId(), curIndv.getName(), curIndv.getGender(),
	    			        	  curIndv.getBirthDate(), curIndv.getAge(), curIndv.isAlive(),
	    				          curIndv.getDeathDate(), curIndv.getChildFIdsAsStr(), 
	    				          curIndv.getSpouseFIdsAsStr());
	    	}
//	    	for(int i=0;i<individualList.size();i++) {
//	    		Individual curIndv = individualList.get(i);
//	    		System.out.format("%1$-10s %2$-25s %3$-7s %4$-12s %5$-5s %6$-7s %7$-12s %8$-20s %9$-20s \n", 
//	    						  curIndv.getId(), curIndv.getName(), curIndv.getGender(),
//	    			        	  curIndv.getBirthDate(), curIndv.getAge(), curIndv.isAlive(),
//	    				          curIndv.getDeathDate(), curIndv.getChildFIdsAsStr(), 
//	    				          curIndv.getSpouseFIdsAsStr());
//	    	}
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

			//Sprint1:US21:Correct gender for role
			for(Family fam: familyList) {
				String husID = fam.husbandId;
				String wifeID = fam.wifeId;
				for(int i=0;i<individualList.size();i++){
					Individual curIndv = individualList.get(i);
					if(curIndv.getId() == husID){
						if(!curIndv.getGender().equals("M")){
							System.out.println("ERROR: INDIVIDUAL: US021: Correct gender for role :" +indv.getId()+" gender is error");
						}
					}else if(curIndv.getId()==wifeID){
						if(!curIndv.getGender().equals("F")){
							System.out.println("ERROR: INDIVIDUAL: US021: Correct gender for role :" +indv.getId()+" gender is error");
						}
					}
				}
			}
			//Sprint1:US22:Unique IDs
			for(int i=0;i<individualList.size();i++) {
	    		Individual curIndv = individualList.get(i);
				String INDI = curIndv.getId();
				if(individualIDList.contains(INDI)){
					System.out.println("ERROR: INDIVIDUAL: US022: Individual ID :" +indv.getId()+" is not unique");
				
				}else{
					individualIDList.add(INDI);
				}
			}
			for(Family fam: familyList) {
				String INDI = fam.getId();
				if(familyIDList.contains(INDI)){
					System.out.println("ERROR: FAMILY: US022: FAMILY ID :" +indv.getId()+" is not unique");
				}else{
					familyIDList.add(INDI);
				}
			}

			// Sprint3 US32:List multiple births wyk
			// List all multiple births in a GEDCOM file
			for(Family fam: familyList) {
				List<String> CurFamchildrenId = fam.getChildrenId();
				if(CurFamchildrenId.size()>1){
					List<Individual> recordchild = new ArrayList<Individual>();
					List<String> recordbirthdate = new ArrayList<String>();
					for(int i=0;i<CurFamchildrenId.size();i++){
						String tmp_childId = CurFamchildrenId.get(i);
						Individual tmpchild = individualList.get(individualIDList.indexOf(tmp_childId));
						String tmpbirthDate = tmpchild.getBirthDate();
						recordchild.add(tmpchild);
						if(recordbirthdate.contains(tmpbirthDate)){
							// meet multiple birth
							Integer record_index= recordbirthdate.indexOf(tmpbirthDate);
							Individual exsited_child = recordchild.get(record_index);
							System.out.println("FAMILY: US32 "+exsited_child.getId()+" and "+tmpchild.getId()+" are multiple birth.");
							recordbirthdate.add(tmpbirthDate);
						}else{
							// add to recprd
							recordbirthdate.add(tmpbirthDate);
						}
					}
				}
			}
			// Sprint3 US33	List orphans wyk
			// List all orphaned children (both parents dead and child < 18 years old) in a GEDCOM file
			for(Family fam: familyList) {
				String fam_husbandId = fam.getHusbandId();
				String fam_wifeId = fam.getWifeId();
				Individual fam_husband = individualList.get(individualIDList.indexOf(fam_husbandId));
				Individual fam_wife = individualList.get(individualIDList.indexOf(fam_wifeId));
				if(fam_husband.deathDate!=null && fam_wife.deathDate!=null){
					// both parents dead
					List<String> CurFamchildrenId = fam.getChildrenId();
					for(int i=0;i<CurFamchildrenId.size();i++){
						String tmp_childId = CurFamchildrenId.get(i);
						Individual tmpchild = individualList.get(individualIDList.indexOf(tmp_childId));
						Integer tmpage =  Integer.parseInt(tmpchild.getAge());
						if(tmpage<18){
							System.out.println("INDIVIDUAL: US33: "+tmp_childId+" is a orphan.");
						};
					}
				}
			}

			// Sprint4 US25	Unique first names in families wyk
			// No more than one child with the same name and birth date should appear in a family
			for(Family fam: familyList) {
				// get all the child
				List<String> CurFamchildrenId = fam.getChildrenId();

				Map<String,String> RecordChildrenName = new HashMap<String,String>(); 
				Map<String,String> RecordChildrenBirthDate = new HashMap<String,String>(); 

				for(int i=0;i<CurFamchildrenId.size();i++){
					String tmp_childId = CurFamchildrenId.get(i);
					Individual tmpchild = individualList.get(individualIDList.indexOf(tmp_childId));
					if(RecordChildrenBirthDate.containsKey(tmpchild.getBirthDate())){
						// if date recorded
						if(RecordChildrenName.containsKey(tmpchild.getName())){
							// if name recorded
							System.out.println("FAMILY: US25: "+tmp_childId+" need no more than one child with same birth and name.");
						}else{
							// no name recorded
							RecordChildrenName.put(tmpchild.getName(), "name");
						}
					}else{
						//date not recorded
						RecordChildrenBirthDate.put(tmpchild.getBirthDate(),"date");
						
						if(RecordChildrenName.containsKey(tmpchild.getName())){
						// if name recorded
							continue;
						}else{
						// if name not recorded
						RecordChildrenName.put(tmpchild.getName(), "name");
						}
					}

				}
			}
			// Sprint4 US15	Fewer than 15 siblings wyk
			// There should be fewer than 15 siblings in a family
			for(Family fam: familyList) {
				// get all the child
				List<String> CurFamchildrenId = fam.getChildrenId();
				if(CurFamchildrenId.size()>=15){
					System.out.println("FAMILY: US15: "+fam.getId()+"should be fewer than 15 siblings");
				}
			}

	    	//Sprint1:US01:Dates before current date-jfl
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
	    	//150 & 14
	    	int year150 = 0;
	    	int year14 = 0;
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
	    	//variable of parents death
	    	boolean Fdeathflag = false;
	    	int Fdeathday = 0;
	    	int Fdeathmonth = 0;
	    	int Fdeathyear = 0;
	    	String Fdeath = null;
	    	boolean Mdeathflag = false ;
	    	int Mdeathday = 0;
	    	int Mdeathmonth = 0;
	    	int Mdeathyear = 0;	
	    	String Mdeath = null;
	    	//variable of parents birth
	    	String Fbirth = null;
	    	int Fbirthyear = 0;
	    	String Mbirth = null;
	    	int Mbirthyear = 0;   
	    	
	    	String fatherName = null;
	    	String sonName = null;
	    	Set<String> nameandbirth = new HashSet<String>(); 
	    	
	    	for(int i=0;i<individualList.size();i++) {
	    		Individual curIndv = individualList.get(i);
	    		//Birthday before current date-jfl
	    		count = 0;
	    		for(String member:curIndv.getBirthDate().split(" ")) {
	    			count++;
	    			if(count==1) {
	    				day = Integer.parseInt(member);
	    			}else if(count==2){
	    				month = tags.get(member);	
	    			}else if(count==3){
	    				year = Integer.parseInt(member);	
	    				if(year>curyear) {
	    					System.out.println("ERROR: INDIVIDUAL: US01: " +curIndv.getId()+": Birthday "+ curIndv.getBirthDate() +"occurs in the future");
	    				}
	    				if(year==curyear && month>curmonth) {
	    					System.out.println("ERROR: INDIVIDUAL: US01: " +curIndv.getId()+": Birthday "+ curIndv.getBirthDate() +"occurs in the future");
	    				}
	    				if(year==curyear && month==curmonth && day>curday) {
	    					System.out.println("ERROR: INDIVIDUAL: US01: " +curIndv.getId()+": Birthday "+ curIndv.getBirthDate() +"occurs in the future");
	    				}
	    				count = 0;
	    			}
	    		}
	    		//Sprint4:US23 Unique name and birth date-jfl
	    		String nb = curIndv.getName() + curIndv.getBirthDate();
	    		if(nameandbirth.contains(nb)) {
	    			System.out.println("ERROR: INDIVIDUAL: US23: " + curIndv.getId()+ ": Name " + curIndv.getName() + "and birth day "+ curIndv.getBirthDate() + "is repeated");
	    		}else {
	    		nameandbirth.add(nb);
	    		}
	    		
			//Sprint1:US07 Less than 150

				for(String member:curIndv.getBirthDate().split(" ")) {
	    			count++;
	    			if(count==1) {
	    				day = Integer.parseInt(member);
	    			}else if(count==2){
	    				month = tags.get(member);	
	    			}else if(count==3){
	    				year = Integer.parseInt(member);
						year150 = year + 150;
						if(!curIndv.getDeathDate().equals("NA")) {
	 	   					deathcount = 0;
		    				for(String deathmember:curIndv.getDeathDate().split(" ")) {
		    					deathcount++;
		    					if(deathcount==1) {
		    						deathday = Integer.parseInt(deathmember);
		    					}else if(deathcount==2){
		    						deathmonth = tags.get(deathmember);	
		    					}else if(deathcount==3){
		    						deathyear = Integer.parseInt(deathmember);	

		    						if(deathyear>year150) {
		    							System.out.println("ERROR: INDIVIDUAL: US07: " +curIndv.getId()+": age "+"bigger than 140 years old");

		    						}
		    						deathcount = 0;
		    					}
		    				}	
	    				}
						count = 0;
					}
				}
	    		//Death day before current date-jfl
	    		if(!curIndv.getDeathDate().equals("NA")) {
	    			deathcount = 0;
		    		for(String deathmember:curIndv.getDeathDate().split(" ")) {
		    			deathcount++;
		    			if(deathcount==1) {
		    				deathday = Integer.parseInt(deathmember);
		    			}else if(deathcount==2){
		    				deathmonth = tags.get(deathmember);	
		    			}else if(deathcount==3){
		    				deathyear = Integer.parseInt(deathmember);	
		    				if(deathyear>curyear) {
		    					System.out.println("ERROR: INDIVIDUAL: US01: " +curIndv.getId()+": Died "+ curIndv.getDeathDate() +"occurs in the future");
		    				}
		    				if(deathyear==curyear && deathmonth>curmonth) {
		    					System.out.println("ERROR: INDIVIDUAL: US01: " +curIndv.getId()+": Died "+ curIndv.getDeathDate() +"occurs in the future");
		    				}
		    				if(deathyear==curyear && deathmonth==curmonth && deathday>curday) {
		    					System.out.println("ERROR: INDIVIDUAL: US01: " +curIndv.getId()+": Died "+ curIndv.getDeathDate() +"occurs in the future");
		    				}
		    				deathcount = 0;
		    			}
		    		}	
	    		}
	    	}
		//Sprint3 US29 List deceased
			
			for(int i=0;i<individualList.size();i++) {
				Individual curIndv = individualList.get(i);
					if(curIndv.getDeathDate().equals("NA")) {
						System.out.println(" INDIVIDUAL: US29: " +curIndv.getId()+"name "+curIndv.getName()+"is deceased individual.");
						}
		
				//Sprint3 US30 List living married
				if(!curIndv.getDeathDate().equals("NA") && curIndv.getMarriageDate().equals("NA")) {
						System.out.println(" INDIVIDUAL: US30: " +curIndv.getId()+"name "+curIndv.getName()+"is living and married.");
					}
				
				
			}
	    	for(Family fam: familyList) {
	    		//Marriage day before current date-jfl
	    		if(!fam.getMarriageDate().equals("NA")) {
	    			marcount = 0;
		    		for(String marmember:fam.getMarriageDate().split(" ")) {
		    			marcount++;
		    			if(marcount==1) {
		    				marday = Integer.parseInt(marmember);
		    			}else if(marcount==2){
		    				marmonth = tags.get(marmember);	
		    			}else if(marcount==3){
		    				maryear = Integer.parseInt(marmember);	
		    				if(maryear>curyear) {
		    					System.out.println("ERROR: INDIVIDUAL: US01: " +fam.getId()+": Married "+ fam.getMarriageDate() +"occurs in the future");
		    				}
		    				if(maryear==curyear && marmonth>curmonth) {
		    					System.out.println("ERROR: INDIVIDUAL: US01: " +fam.getId()+": Married "+ fam.getMarriageDate() +"occurs in the future");
		    				}
		    				if(maryear==curyear && marmonth==curmonth && marday>curday) {
		    					System.out.println("ERROR: INDIVIDUAL: US01: " +fam.getId()+": Married "+ fam.getMarriageDate() +"occurs in the future");
		    				}
		    				marcount = 0;
		    			}
		    		}	
	    		}
			//Sprint1:US10 Marriage after 14	
				
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
	    				for(String member:curIndv.getBirthDate().split(" ")) {
	    	    			count++;
	    	    			if(count==1) {
	    	    				day = Integer.parseInt(member);
	    	    			}else if(count==2){
	    	    				month = tags.get(member);	
	    	    			}else if(count==3){
	    	    				year = Integer.parseInt(member);
								year14 = year +14;	
	    	    				if(year14>maryear) {
	    	    					System.out.println("ERROR: INDIVIDUAL: US10: " +fam.getId()+"Married "+ fam.getMarriageDate() +"marriage Before 14 years old");
	    	    				}
	    	    				if(year==maryear && month>marmonth) {
	    	    					System.out.println("ERROR: INDIVIDUAL: US10: " +fam.getId()+"Married "+ fam.getMarriageDate() +"marriage Before 14 years old");
	    	    				}
	    	    				if(year==maryear && month==marmonth && day>marday) {
	    	    					System.out.println("ERROR: INDIVIDUAL: US10: " +fam.getId()+"Married "+ fam.getMarriageDate() +"marriage Before 14 years old");
	    	    				}
	    	    				count = 0;
	    	    			}
	    				}
	    			}
	    			if(curIndv.getId().equals(fam.getWifeId())) {
	    				for(String member:curIndv.getBirthDate().split(" ")) {
	    	    			count++;
	    	    			if(count==1) {
	    	    				day = Integer.parseInt(member);
	    	    			}else if(count==2){
	    	    				month = tags.get(member);	
	    	    			}else if(count==3){
	    	    				year = Integer.parseInt(member);
								year14 = year +14;	
	    	    				if(year>maryear) {
	    	    					System.out.println("ERROR: INDIVIDUAL: US10: " +fam.getId()+"Married "+ fam.getMarriageDate() +"marriage Before 14 years old");
	    	    				}
	    	    				if(year==maryear && month>marmonth) {
	    	    					System.out.println("ERROR: INDIVIDUAL: US10: " +fam.getId()+"Married "+ fam.getMarriageDate() +"marriage Before 14 years old");
	    	    				}
	    	    				if(year==maryear && month==marmonth && day>marday) {
	    	    					System.out.println("ERROR: INDIVIDUAL: US10: " +fam.getId()+"Married "+ fam.getMarriageDate() +"marriage Before 14 years old");
	    	    				}
	    	    				count = 0;
	    	    			}
	    				}
	    			}
				}
//				Sprint2:US04 Marriage before divorce
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
				if(!fam.getDivorceDate().equals("NA")) {
	    			divcount = 0;
		    		for(String divmember:fam.getDivorceDate().split(" ")) {
		    			divcount++;
		    			if(divcount==1) {
		    				divday = Integer.parseInt(divmember);
		    			}else if(divcount==2){
		    				divmonth = tags.get(divmember);	
		    			}else if(divcount==3){
		    				divyear = Integer.parseInt(divmember);	
		    				if(divyear<maryear) {
		    					System.out.println("ERROR: INDIVIDUAL: US04: " +fam.getId()+": Divorced should after marriage");
		    				}
		    				if(divyear==maryear && divmonth<marmonth) {
		    					System.out.println("ERROR: INDIVIDUAL: US04: " +fam.getId()+": Divorced should after marriage");
		    				}
		    				if(divyear==maryear && divmonth==marmonth && divday<marday) {
		    					System.out.println("ERROR: INDIVIDUAL: US04: " +fam.getId()+": Divorced should after marriage");
		    				}
		    				divcount = 0;
		    			}
		    		}
	    		}

				//Sprint2:US06 divorce before death 
				count=0;
	    		for(int i=0;i<individualList.size();i++) {
	    			Individual curIndv = individualList.get(i);
	    				if(!curIndv.getDeathDate().equals("NA")) {
	    					for(String member:curIndv.getDeathDate().split(" ")) {
		    						count++;
		    	    			if(count==1) {
		    	    				deathday = Integer.parseInt(member);
		    	    			}else if(count==2){
		    	    				deathmonth = tags.get(member);	
		    	    			}else if(count==3){
		    	    				deathyear = Integer.parseInt(member);
		    	    				count = 0;
		    	    			}
		    				}
	    				}
	    		}
				if(!fam.getDivorceDate().equals("NA")) {
	    			divcount = 0;
		    		for(String divmember:fam.getDivorceDate().split(" ")) {
		    			divcount++;
		    			if(divcount==1) {
		    				divday = Integer.parseInt(divmember);
		    			}else if(divcount==2){
		    				divmonth = tags.get(divmember);	
		    			}else if(divcount==3){
		    				divyear = Integer.parseInt(divmember);	
		    				if(divyear>deathyear) {
		    					System.out.println("ERROR: INDIVIDUAL: US06: " +fam.getId()+": Divorced should before death");
		    				}
		    				if(divyear==deathyear && divmonth>deathmonth) {
		    					System.out.println("ERROR: INDIVIDUAL: US04: " +fam.getId()+": Divorced should before death");
		    				}
		    				if(divyear==deathyear && divmonth==deathmonth && divday>deathday) {
		    					System.out.println("ERROR: INDIVIDUAL: US04: " +fam.getId()+": Divorced should before death");
		    				}
		    				divcount = 0;
		    			}
		    		}
	    		}
	    		//Divorce day before current date-jfl
	    		if(!fam.getDivorceDate().equals("NA")) {
	    			divcount = 0;
		    		for(String divmember:fam.getDivorceDate().split(" ")) {
		    			divcount++;
		    			if(divcount==1) {
		    				divday = Integer.parseInt(divmember);
		    			}else if(divcount==2){
		    				divmonth = tags.get(divmember);	
		    			}else if(divcount==3){
		    				divyear = Integer.parseInt(divmember);	
		    				if(divyear>curyear) {
		    					System.out.println("ERROR: INDIVIDUAL: US01: " +fam.getId()+": Divorced "+ fam.getDivorceDate() +"occurs in the future");
		    				}
		    				if(divyear==curyear && divmonth>curmonth) {
		    					System.out.println("ERROR: INDIVIDUAL: US01: " +fam.getId()+": Divorced "+ fam.getDivorceDate() +"occurs in the future");
		    				}
		    				if(divyear==curyear && divmonth==curmonth && divday>curday) {
		    					System.out.println("ERROR: INDIVIDUAL: US01: " +fam.getId()+": Divorced "+ fam.getDivorceDate() +"occurs in the future");
		    				}
		    				divcount = 0;
		    			}
		    		}
	    		}
	    		//Sprint1:US02:Birthday before marriage-jfl
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
	    				for(String member:curIndv.getBirthDate().split(" ")) {
	    	    			count++;
	    	    			if(count==1) {
	    	    				day = Integer.parseInt(member);
	    	    			}else if(count==2){
	    	    				month = tags.get(member);	
	    	    			}else if(count==3){
	    	    				year = Integer.parseInt(member);	
	    	    				if(year>maryear) {
	    	    					System.out.println("ERROR: FAMILY: US02: " +fam.getId()+": Husband's Birthday "+ curIndv.getBirthDate() +"after marriage date " + fam.getMarriageDate());
	    	    				}
	    	    				if(year==maryear && month>marmonth) {
	    	    					System.out.println("ERROR: FAMILY: US02: " +fam.getId()+": Husband's Birthday "+ curIndv.getBirthDate() +"after marriage date " + fam.getMarriageDate());
	    	    				}
	    	    				if(year==maryear && month==marmonth && day>marday) {
	    	    					System.out.println("ERROR: FAMILY: US02: " +fam.getId()+": Husband's Birthday "+ curIndv.getBirthDate() +"after marriage date " + fam.getMarriageDate());
	    	    				}
	    	    				count = 0;
	    	    			}
	    				}
	    			}
	    			if(curIndv.getId().equals(fam.getWifeId())) {
	    				for(String member:curIndv.getBirthDate().split(" ")) {
	    	    			count++;
	    	    			if(count==1) {
	    	    				day = Integer.parseInt(member);
	    	    			}else if(count==2){
	    	    				month = tags.get(member);	
	    	    			}else if(count==3){
	    	    				year = Integer.parseInt(member);	
	    	    				if(year>maryear) {
	    	    					System.out.println("ERROR: FAMILY: US02: " +fam.getId()+": Wife's Birthday "+ curIndv.getBirthDate() +"after marriage date " + fam.getMarriageDate());
	    	    				}
	    	    				if(year==maryear && month>marmonth) {
	    	    					System.out.println("ERROR: FAMILY: US02: " +fam.getId()+": Wife's Birthday "+ curIndv.getBirthDate() +"after marriage date " + fam.getMarriageDate());
	    	    				}
	    	    				if(year==maryear && month==marmonth && day>marday) {
	    	    					System.out.println("ERROR: FAMILY: US02: " +fam.getId()+": Wife's Birthday "+ curIndv.getBirthDate() +"after marriage date " + fam.getMarriageDate());
	    	    				}
	    	    				count = 0;
	    	    			}
	    				}
	    			}
		    		//Sprint2:US08:Birth after marriage of parents-jfl
	    			for(String ChildId : fam.getChildrenId()){
	    				String s = ChildId.replaceAll("\'", "");
	    				if(curIndv.getId().equals(s)) {
	    					for(String member:curIndv.getBirthDate().split(" ")) {
	    						count++;
	    						if(count==1) {
	    							day = Integer.parseInt(member);
	    						}else if(count==2){
	    							month = tags.get(member);	
	    						}else if(count==3){
	    							year = Integer.parseInt(member);	
	    							if(year<maryear) {
			    	    				System.out.println("ERROR: FAMILY: US08: " +fam.getId()+": Child's Birthday "+ curIndv.getBirthDate() +"before marriage date of parents " + fam.getMarriageDate());
			    	    			}
			    	    			if(year==maryear && month<marmonth) {
			    	    				System.out.println("ERROR: FAMILY: US08: " +fam.getId()+": Child's Birthday "+ curIndv.getBirthDate() +"before marriage date of parents " + fam.getMarriageDate());
			    	    			}
			    	    			if(year==maryear && month==marmonth && day<marday) {
			    	    				System.out.println("ERROR: FAMILY: US08: " +fam.getId()+": Child's Birthday "+ curIndv.getBirthDate() +"before marriage date of parents " + fam.getMarriageDate());
			    	    			}
			    	    			count = 0;
	    						}
	    					}
	    				}
	    			}
		    		//Sprint4:US16:Male last names-jfl
	    			for(String ChildId : fam.getChildrenId()){
	    				String s = ChildId.replaceAll("\'", "");
	    				if(curIndv.getId().equals(s) && curIndv.getGender().equals("M ")) {
	    					for(String name:fam.getHusbandName().split(" ")) {
	    						fatherName = name;
	    					}
	    					for(String name:curIndv.getName().split(" ")) {
	    						sonName = name;
	    					}   					
	    					if(!fatherName.equals(sonName)) {
	    						System.out.println("ERROR: FAMILY: US16: " +fam.getId()+": Son's last name "+ sonName +" is different from his father " + fatherName);
	    					}
	    				}
	    			}
	    		}	
	    		//Sprint2:US09:Birth before death of parents-jfl
	    		count=0;
	    		for(int i=0;i<individualList.size();i++) {
	    			Individual curIndv = individualList.get(i);
	    			if(curIndv.getId().equals(fam.getHusbandId())) {
	    				if(!curIndv.getDeathDate().equals("NA")) {
	    					Fdeathflag = true;
	    					Fdeath = curIndv.getDeathDate();
	    					for(String member:curIndv.getDeathDate().split(" ")) {
		    						count++;
		    	    			if(count==1) {
		    	    				Fdeathday = Integer.parseInt(member);
		    	    			}else if(count==2){
		    	    				Fdeathmonth = tags.get(member);	
		    	    			}else if(count==3){
		    	    				Fdeathyear = Integer.parseInt(member);
		    	    				count = 0;
		    	    			}
		    				}
	    				}else {
	    					Fdeathflag = false;
	    				}
	    			}
	    		}
	    		count=0;
	    		for(int i=0;i<individualList.size();i++) {
	    			Individual curIndv = individualList.get(i);
	    			if(curIndv.getId().equals(fam.getWifeId())) {
	    				if(!curIndv.getDeathDate().equals("NA")) {
	    					Mdeathflag = true;
	    					Mdeath = curIndv.getDeathDate();
		    				for(String member:curIndv.getDeathDate().split(" ")) {
		    	    			count++;
		    	    			if(count==1) {
		    	    				Mdeathday = Integer.parseInt(member);
		    	    			}else if(count==2){
		    	    				Mdeathmonth = tags.get(member);	
		    	    			}else if(count==3){
		    	    				Mdeathyear = Integer.parseInt(member);
		    	    				count = 0;
		    	    			}
		    				}
	    				}else {
	    					Mdeathflag = false;
	    				}
	    			}
	    		}
	    		if(Fdeathflag == true||Mdeathflag == true) {
		    		count = 0;
		    		for(int i=0;i<individualList.size();i++) {
		    			Individual curIndv = individualList.get(i);
		    			for(String ChildId : fam.getChildrenId()){
		    				String s = ChildId.replaceAll("\'", "");
		    				if(curIndv.getId().equals(s)) {
		    					for(String member:curIndv.getBirthDate().split(" ")) {
		    						count++;
		    						if(count==1) {
		    							day = Integer.parseInt(member);
		    						}else if(count==2){
		    							month = tags.get(member);	
		    						}else if(count==3){
		    							year = Integer.parseInt(member);	
		    							if(Fdeathflag == true) {
			    							if(year-Fdeathyear>=2) {
					    	    				System.out.println("ERROR: FAMILY: US09: " +fam.getId()+": Child's Birthday "+ curIndv.getBirthDate() +"after death of father(before 9 months after death of father) " + Fdeath);
					    	    			}
					    	    			if(year-Fdeathyear==1) {
					    	    				if(Fdeathmonth+9>12) {
					    	    					if(Fdeathmonth-3<month) {
					    	    						System.out.println("ERROR: FAMILY: US09: " +fam.getId()+": Child's Birthday "+ curIndv.getBirthDate() +"after death of father(before 9 months after death of father) " + Fdeath);
					    	    					}
					    	    				}else {
					    	    					System.out.println("ERROR: FAMILY: US09: " +fam.getId()+": Child's Birthday "+ curIndv.getBirthDate() +"after death of father(before 9 months after death of father) " + Fdeath);
					    	    				}
					    	    			}
					    	    			if(year==Fdeathyear && month - Fdeathmonth>9) {
					    	    				System.out.println("ERROR: FAMILY: US09: " +fam.getId()+": Child's Birthday "+ curIndv.getBirthDate() +"after death of father(before 9 months after death of father) " + Fdeath);
					    	    			}
		    							}
		    							if(Mdeathflag == true) {
			    							if(year>Mdeathyear) {
					    	    				System.out.println("ERROR: FAMILY: US09: " +fam.getId()+": Child's Birthday "+ curIndv.getBirthDate() +"after death of mother " + Mdeath);
					    	    			}
					    	    			if(year==Mdeathyear && month > Mdeathmonth) {
					    	    				System.out.println("ERROR: FAMILY: US09: " +fam.getId()+": Child's Birthday "+ curIndv.getBirthDate() +"after death of mother " + Mdeath);
					    	    			}
					    	    			if(year==Mdeathyear && month == Mdeathmonth && day > Mdeathday) {
					    	    				System.out.println("ERROR: FAMILY: US09: " +fam.getId()+": Child's Birthday "+ curIndv.getBirthDate() +"after death of mother " + Mdeath);
					    	    			}
		    							}
				    	    			count = 0;
		    						}
		    					}
		    				}
		    			}
	    			}
	    		}
	    		//Sprint3:US12:Parents not too old-jfl
	    		count=0;
	    		for(int i=0;i<individualList.size();i++) {
	    			Individual curIndv = individualList.get(i);
	    			if(curIndv.getId().equals(fam.getHusbandId())) {
	    				Fbirth = curIndv.getBirthDate();
	    				for(String member:curIndv.getBirthDate().split(" ")) {
		    					count++;
		    				if(count==3){
		    	    			Fbirthyear = Integer.parseInt(member);
		    	    			count = 0;
		    	    		}
		    			}
	    			}
	    		}
	    		count=0;
	    		for(int i=0;i<individualList.size();i++) {
	    			Individual curIndv = individualList.get(i);
	    			if(curIndv.getId().equals(fam.getWifeId())) {
	    				Mbirth = curIndv.getBirthDate();
	    				for(String member:curIndv.getBirthDate().split(" ")) {
		    					count++;
		    				if(count==3){
		    	    			Mbirthyear = Integer.parseInt(member);
		    	    			count = 0;
		    	    		}
		    			}
	    			}
	    		}
	    		count = 0;
	    		for(int i=0;i<individualList.size();i++) {
	    			Individual curIndv = individualList.get(i);
	    			for(String ChildId : fam.getChildrenId()){
	    				String s = ChildId.replaceAll("\'", "");
	    				if(curIndv.getId().equals(s)) {
	    					for(String member:curIndv.getBirthDate().split(" ")) {
	    						count++;
	    						if(count==3){
	    							year = Integer.parseInt(member);	
		    						if(year-Fbirthyear>80) {
				    	    			System.out.println("ERROR: FAMILY: US12: " +fam.getId()+": Father's Birthday "+Fbirth  +"more than 80 years older than his children " + curIndv.getBirthDate());
				    	    		}
		    						if(year-Mbirthyear>60) {
		    							System.out.println("ERROR: FAMILY: US12: " +fam.getId()+": Mother's Birthday "+ Mbirth +"more than 60 years older than his children " + curIndv.getBirthDate());
				    	    		}
			    	    			count = 0;
	    						}
	    					}
	    				}
	    			}
    			}
    			//Sprint3:US13:Siblings spacing-jfl
    			if(fam.getChildrenId().size()>1) {
    				int[][] siblings = new int [fam.getChildrenId().size()][2];
    				int indexs = 0;
    				for(int i=0;i<individualList.size();i++) {
    					Individual curIndv = individualList.get(i);
    					for(String ChildId : fam.getChildrenId()){
    						String s = ChildId.replaceAll("\'", "");
    						if(curIndv.getId().equals(s)) {
    							for(String member:curIndv.getBirthDate().split(" ")) {
    								count++;
    								if(count==2){
    									siblings[indexs][1] = tags.get(member);	
    								}else if(count==3){
    									siblings[indexs][0] = Integer.parseInt(member);	
    									count = 0;
    								}
    							}
    							indexs++;
    						}
    					}	
    				}
	    			Arrays.sort(siblings,(a,b)->{
	    				if(a[0]==b[0]){
	    					return a[1]-b[1];
	    				}
	    				return a[0]-b[0];
	    				
	    			});
	    			for(int i = 0;i<siblings.length-1;i++) {
	    				if(siblings[i][0] == siblings[i+1][0]) {
	    					if(Math.abs(siblings[i][1] - siblings[i][1]) < 8) {
	    						System.out.println("ERROR: FAMILY: US13: " +fam.getId()+": Birth dates of siblings should be more than 8 months ");
	    					}
	    				}else {
	    					if(siblings[i][0]+1 == siblings[i+1][0]) {
	    						if(12-siblings[i][1]+ siblings[i+1][1]<8) {
	    							System.out.println("ERROR: FAMILY: US13: " +fam.getId()+": Birth dates of siblings should be more than 8 months ");
	    						}
	    					}
	    				}
	    			}

    			}
	    	}
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
	    							if(year<maryear) {
	    								System.out.println("ERROR: FAMILY: US05: " +fam.getId()+" :Marriage date " + fam.getMarriageDate()+"before husband's DeathDate "+ curIndv.getDeathDate() );
	    							}
	    							if(year==maryear && month<marmonth) {
	    								System.out.println("ERROR: FAMILY: US05: " +fam.getId()+" :Marriage date " + fam.getMarriageDate()+"before husband's DeathDate "+ curIndv.getDeathDate() );
	    							}
	    							if(year==maryear && month==marmonth && day<marday) {
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
	    							if(year<maryear) {
		    	    					System.out.println("ERROR: FAMILY: US05: " +fam.getId()+" :Marriage date " + fam.getMarriageDate()+"before wife's DeathDate "+ curIndv.getDeathDate() );
		    	    				}
		    	    				if(maryear==year && month<marmonth) {
		    	    					System.out.println("ERROR: FAMILY: US05: " +fam.getId()+ ":Marriage date " + fam.getMarriageDate()+"before wife's DeathDate "+ curIndv.getDeathDate());
		    	    				}
		    	    				if(maryear==year && marmonth==month && day<marday) {
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
	    	
	    	
	    	for(int i=0;i<individualList.size();i++) {
	    		Individual curIndv = individualList.get(i);
	    		//US35List recent births
	    		count = 0;
	    		for(String member:curIndv.getBirthDate().split(" ")) {
	    			count++;
	    			if(count==1) {
	    				day = Integer.parseInt(member);
	    			}else if(count==2){
	    				month = tags.get(member);	
	    			}else if(count==3){
	    				year = Integer.parseInt(member);	
	    				
	    				if(year==curyear &&month==curmonth&&curday-day<30&&curday-day>0) {
	    					System.out.println(" INDIVIDUAL: US35: " +curIndv.getId()+"name "+curIndv.getName()+": Birthday "+ curIndv.getBirthDate() +"birth recently");
	    				}
	    				count = 0;
	    			}
	    		}

	    		//US36 List recent death
	    		if(!curIndv.getDeathDate().equals("NA")) {
	    			deathcount = 0;
		    		for(String deathmember:curIndv.getDeathDate().split(" ")) {
		    			deathcount++;
		    			if(deathcount==1) {
		    				deathday = Integer.parseInt(deathmember);
		    			}else if(deathcount==2){
		    				deathmonth = tags.get(deathmember);	
		    			}else if(deathcount==3){
		    				deathyear = Integer.parseInt(deathmember);	
		    				
		    				if(deathyear==curyear && deathmonth==curmonth && curday-deathday<30&&curday-deathday>0) {
		    					System.out.println(" INDIVIDUAL: US36: " +curIndv.getId()+"name "+curIndv.getName()+": Birthday "+ curIndv.getBirthDate() +"death recently");
		    				}
		    				deathcount = 0;
		    			}
		    		}	
	    		}
}
	    	
	     	
	       	for(int i=0;i<individualList.size();i++) 
	       	{
	       		
	    		Individual curIndv = individualList.get(i);
	    		
	    		//US38 List upcoming birthdays
	    		
	    		count = 0;
	    		
	    		for(String member:curIndv.getBirthDate().split(" ")) 
	    		{
	    			count++;
	    			if(count==1)
	    			{
	    				
	    				day = Integer.parseInt(member);
	    				
	    			}else if(count==2)
	    			{
	    				month = tags.get(member);
	    				
	    			}else if(count==3)
	    			{
	    				year = Integer.parseInt(member);	
	    				
	    				if(month==curmonth&&curday-day<30&&curday-day>0) 
	    					
	    				{
	    					System.out.println(" INDIVIDUAL: US38: " +curIndv.getId()+"name "+curIndv.getName()+": Birthday "+ curIndv.getBirthDate() +"upcoming birthdays");
	    				}
                        if(curmonth-month==1&&curday-day>-30&&curday-day<0) 
	    					
	    				{
	    					System.out.println(" INDIVIDUAL: US38: " +curIndv.getId()+"name "+curIndv.getName()+": Birthday "+ curIndv.getBirthDate() +"upcoming birthdays");
	    				}
	    				count = 0;
	    			}
	    		}
	       	}
	    	
	       	
	       	for(Family fam: familyList) 
	       	{
	       	//US39 List upcoming anniversaries
	    		
	    		if(!fam.getMarriageDate().equals("NA")) 
	    		{
	    			marcount = 0;
		    		for(String marmember:fam.getMarriageDate().split(" ")) {
		    			marcount++;
		    			
		    			if(marcount==1) 
		    			{
		    				marday = Integer.parseInt(marmember);
		    			}
		    			else if(marcount==2)
		    			{
		    				marmonth = tags.get(marmember);	
		    			}else if(marcount==3)
		    			{
		    				maryear = Integer.parseInt(marmember);	
		    				if( curmonth-marmonth==1 && curday-marday>-30&&curday-marday<0)
		    				{
			    					System.out.println(" FAMILY: US39: " +fam.getId()+fam.getHusbandName()+" "+fam.getWifeName()+": Married "+ fam.getMarriageDate() +"upcoming anniversaries");
			    				
		    				}
		    				else if(  marmonth==curmonth && marday-curday<30&&marday-curday>0) 
		    				{
		    					   System.out.println(" FAMILY: US39: " +fam.getId() +fam.getHusbandName()+" "+fam.getWifeName()+": Married "+ fam.getMarriageDate() +"upcoming anniversaries");
		    			
		    				}
		    				
		    			
		    				marcount = 0;
		    			}
		    		}	
	    		}
	       	}
	    		
	      //Sprint4:US34:List large age differences
	     	for(Family fam: familyList) 
	       	if(!fam.getMarriageDate().equals("NA")) {
    		count=0;
    		for(int i=0;i<individualList.size();i++) {
    			Individual curIndv = individualList.get(i);
    			if(curIndv.getId().equals(fam.getHusbandId())) {
    				Fbirth = curIndv.getBirthDate();
    				for(String member:curIndv.getBirthDate().split(" ")) {
	    					count++;
	    				if(count==3){
	    	    			Fbirthyear = Integer.parseInt(member);
	    	    			count = 0;
	    	    		}
	    			}
    			}
    		}
    		count=0;
    		for(int i=0;i<individualList.size();i++) {
    			Individual curIndv = individualList.get(i);
    			if(curIndv.getId().equals(fam.getWifeId())) {
    				Mbirth = curIndv.getBirthDate();
    				for(String member:curIndv.getBirthDate().split(" ")) {
	    					count++;
	    				if(count==3){
	    	    			Mbirthyear = Integer.parseInt(member);
	    	    			count = 0;
	    	    		}
	    			}
    			}
    		}
    		count = 0;
               int a=curyear-Mbirthyear;
               int b=curyear-Fbirthyear;
    		if(b>0&&a/b>2) {
                	
	    		System.out.println(" FAMILY: US34: " +fam.getId()+fam.getHusbandName()+" "+fam.getWifeName() +"List large age differences");
	    	}	       	
    		if(a>0&&b/a>2) {
            	
    		System.out.println(" FAMILY: US34: " +fam.getId()+fam.getHusbandName()+" "+fam.getWifeName() +"List large age differences");
    	}	       	
          }
	    	
	    	
	     	//Sprint4:US42:Reject illegitimate dates
	     	
	     	
	     	
	     	//check birth day
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
	    				if(month==12&&day>31) {
	    					System.out.println("ERROR : US42: " +curIndv.getId()+": Birthday "+ curIndv.getBirthDate() +"Reject illegitimate dates");
	    				}
	    				if(month==10&&day>31) {
	    					System.out.println("ERROR : US42: " +curIndv.getId()+": Birthday "+ curIndv.getBirthDate() +"Reject illegitimate dates");
	    				}
	    				if(month==8&&day>31) {
	    					System.out.println("ERROR : US42: " +curIndv.getId()+": Birthday "+ curIndv.getBirthDate() +"Reject illegitimate dates");
	    				}
	    				if(month==7&&day>31) {
	    					System.out.println("ERROR : US42: " +curIndv.getId()+": Birthday "+ curIndv.getBirthDate() +"Reject illegitimate dates");
	    				}
	    				if(month==5&&day>31) {
	    					System.out.println("ERROR : US42: " +curIndv.getId()+": Birthday "+ curIndv.getBirthDate() +"Reject illegitimate dates");
	    				}
	    				if(month==3&&day>31) {
	    					System.out.println("ERROR : US42: " +curIndv.getId()+": Birthday "+ curIndv.getBirthDate() +"Reject illegitimate dates");
	    				}
	    				if(month==1&&day>31) {
	    					System.out.println("ERROR : US42: " +curIndv.getId()+": Birthday "+ curIndv.getBirthDate() +"Reject illegitimate dates");
	    				}
	    				if(month>12||day<0||month<0) {
	    					System.out.println("ERROR: US42: " +curIndv.getId()+": Birthday "+ curIndv.getBirthDate() +"Reject illegitimate dates");
	    				}
	    				if(month==11&&day>30) {
	    					System.out.println("ERROR: US42: " +curIndv.getId()+": Birthday "+ curIndv.getBirthDate() +"Reject illegitimate dates");
	    				}
	    				if(month==4&&day>30) {
	    					System.out.println("ERROR: US42: " +curIndv.getId()+": Birthday "+ curIndv.getBirthDate() +"Reject illegitimate dates");
	    				}
	    				if(month==6&&day>30) {
	    					System.out.println("ERROR: US42: " +curIndv.getId()+": Birthday "+ curIndv.getBirthDate() +"Reject illegitimate dates");
	    				}
	    				if(month==9&&day>30) {
	    					System.out.println("ERROR: US42: " +curIndv.getId()+": Birthday "+ curIndv.getBirthDate() +"Reject illegitimate dates");
	    				}
	    				if(month==2&&day>28) {
	    					System.out.println("ERROR: US42: " +curIndv.getId()+": Birthday "+ curIndv.getBirthDate() +"Reject illegitimate dates");
	    				}
	    				count = 0;
	    			}
	    		}
	        //check deathday
    		if(!curIndv.getDeathDate().equals("NA")) {
    			deathcount = 0;
	    		for(String deathmember:curIndv.getDeathDate().split(" ")) {
	    			deathcount++;
	    			if(deathcount==1) {
	    				deathday = Integer.parseInt(deathmember);
	    			}else if(deathcount==2){
	    				deathmonth = tags.get(deathmember);	
	    			}else if(deathcount==3){
	    				deathyear = Integer.parseInt(deathmember);	
	    				if(deathmonth==1&&deathday>31) {
	    					System.out.println("ERROR: US42: " +curIndv.getId()+": Died "+ curIndv.getDeathDate() +"Reject illegitimate dates");	    					
	    				}
	    				if(deathmonth==3&&deathday>31) {
	    					System.out.println("ERROR: US42: " +curIndv.getId()+": Died "+ curIndv.getDeathDate() +"Reject illegitimate dates");	    					
	    				}
	    				if(deathmonth==5&&deathday>31) {
	    					System.out.println("ERROR: US42: " +curIndv.getId()+": Died "+ curIndv.getDeathDate() +"Reject illegitimate dates");	    					
	    				}
	    				if(deathmonth==7&&deathday>31) {
	    					System.out.println("ERROR: US42: " +curIndv.getId()+": Died "+ curIndv.getDeathDate() +"Reject illegitimate dates");	    					
	    				}
	    				if(deathmonth==8&&deathday>31) {
	    					System.out.println("ERROR: US42: " +curIndv.getId()+": Died "+ curIndv.getDeathDate() +"Reject illegitimate dates");	    					
	    				}
	    				if(deathmonth==10&&deathday>31) {
	    					System.out.println("ERROR: US42: " +curIndv.getId()+": Died "+ curIndv.getDeathDate() +"Reject illegitimate dates");	    					
	    				}
	    				if(deathmonth==12&&deathday>31) {
	    					System.out.println("ERROR: US42: " +curIndv.getId()+": Died "+ curIndv.getDeathDate() +"Reject illegitimate dates");	    					
	    				}
	    				if( deathmonth>12||deathday<0||deathmonth<0) {
	    					System.out.println("ERROR: US42: " +curIndv.getId()+": Died "+ curIndv.getDeathDate() +"Reject illegitimate dates");
	    				}
	    				if(deathmonth==4&&deathday>30) {
	    					System.out.println("ERROR: US42: " +curIndv.getId()+": Died "+ curIndv.getDeathDate() +"Reject illegitimate dates");
	    				}
	    				if(deathmonth==6&&deathday>30) {
	    					System.out.println("ERROR: US42: " +curIndv.getId()+": Died "+ curIndv.getDeathDate() +"Reject illegitimate dates");
	    				}
	    				if(deathmonth==9&&deathday>30) {
	    					System.out.println("ERROR: US42: " +curIndv.getId()+": Died "+ curIndv.getDeathDate() +"Reject illegitimate dates");
	    				}
	    				if(deathmonth==11&&deathday>30) {
	    					System.out.println("ERROR: US42: " +curIndv.getId()+": Died "+ curIndv.getDeathDate() +"Reject illegitimate dates");
	    				}
	    				if(deathmonth==2&&deathday>28) {
	    					System.out.println("ERROR: US42: " +curIndv.getId()+": Died "+ curIndv.getDeathDate() +"Reject illegitimate dates");
	    				}
	    				deathcount = 0;
	    			}
	    		}	
    		}
    	}
	     	//check marriage date
	     	for(Family fam: familyList) {
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
    			if(marmonth>12||marday<0||marmonth<0) {
					System.out.println("ERROR : US42: " +fam.getId()+": Married "+ fam.getMarriageDate() +"Reject illegitimate dates");
				}
				if(marmonth==2&&marday>28) {
					System.out.println("ERROR : US42:  " +fam.getId()+": Married "+ fam.getMarriageDate() +"Reject illegitimate dates");
				}
				if(marmonth==1&&marday>31) {
					System.out.println("ERROR : US42: " +fam.getId()+": Married "+ fam.getMarriageDate() +"Reject illegitimate dates");
				}
				if(marmonth==3&&marday>31) {
					System.out.println("ERROR : US42: " +fam.getId()+": Married "+ fam.getMarriageDate() +"Reject illegitimate dates");
				}
				if(marmonth==5&&marday>31) {
					System.out.println("ERROR : US42: " +fam.getId()+": Married "+ fam.getMarriageDate() +"Reject illegitimate dates");
				}
				if(marmonth==7&&marday>31) {
					System.out.println("ERROR : US42: " +fam.getId()+": Married "+ fam.getMarriageDate() +"Reject illegitimate dates");
				}
				if(marmonth==8&&marday>31) {
					System.out.println("ERROR : US42: " +fam.getId()+": Married "+ fam.getMarriageDate() +"Reject illegitimate dates");
				}
				if(marmonth==10&&marday>31) {
					System.out.println("ERROR : US42: " +fam.getId()+": Married "+ fam.getMarriageDate() +"Reject illegitimate dates");
				}
				if(marmonth==12&&marday>31) {
					System.out.println("ERROR : US42: " +fam.getId()+": Married "+ fam.getMarriageDate() +"Reject illegitimate dates");
				}
				if(marmonth==4&&marday>30) {
					System.out.println("ERROR : US42: " +fam.getId()+": Married "+ fam.getMarriageDate() +"Reject illegitimate dates");
				}
				if(marmonth==6&&marday>30) {
					System.out.println("ERROR : US42: " +fam.getId()+": Married "+ fam.getMarriageDate() +"Reject illegitimate dates");
				}
				if(marmonth==9&&marday>30) {
					System.out.println("ERROR : US42: " +fam.getId()+": Married "+ fam.getMarriageDate() +"Reject illegitimate dates");
				}
				if(marmonth==11&&marday>30) {
					System.out.println("ERROR : US42: " +fam.getId()+": Married "+ fam.getMarriageDate() +"Reject illegitimate dates");
				}
    		}
	     	}
	     	for(Family fam: familyList) {
    		// check divorce date
			if(!fam.getDivorceDate().equals("NA")) {
    			divcount = 0;
	    		for(String divmember:fam.getDivorceDate().split(" ")) {
	    			divcount++;
	    			if(divcount==1) {
	    				divday = Integer.parseInt(divmember);
	    			}else if(divcount==2){
	    				divmonth = tags.get(divmember);	
	    			}else if(divcount==3){
	    				divyear = Integer.parseInt(divmember);	
	    				if(divmonth>12||divday<0||divmonth<0) {
	    					System.out.println("ERROR : US42: " +fam.getId()+fam.getDivorceDate()+": Reject illegitimate dates");
	    				}
	    				if(divmonth==2&&divday>28) {
	    					System.out.println("ERROR : US42: " +fam.getId()+fam.getDivorceDate()+": DReject illegitimate dates");
	    				}
	    				if(divmonth==4&&divday>30) {
	    					System.out.println("ERROR : US42: " +fam.getId()+fam.getDivorceDate()+": Reject illegitimate dates");
	    				}
	    				if(divmonth==6&&divday>30) {
	    					System.out.println("ERROR : US42: " +fam.getId()+fam.getDivorceDate()+": Reject illegitimate dates");
	    				}
	    				if(divmonth==9&&divday>30) {
	    					System.out.println("ERROR : US42: " +fam.getId()+fam.getDivorceDate()+": Reject illegitimate dates");
	    				}
	    				if(divmonth==11&&divday>30) {
	    					System.out.println("ERROR : US42: " +fam.getId()+fam.getDivorceDate()+": Reject illegitimate dates");
	    				}
	    				if(divmonth==1&&divday>31) {
	    					System.out.println("ERROR : US42: " +fam.getId()+fam.getDivorceDate()+": Reject illegitimate dates");
	    				}
	    				if(divmonth==3&&divday>31) {
	    					System.out.println("ERROR : US42: " +fam.getId()+fam.getDivorceDate()+": Reject illegitimate dates");
	    				}
	    				if(divmonth==5&&divday>31) {
	    					System.out.println("ERROR : US42: " +fam.getId()+fam.getDivorceDate()+": Reject illegitimate dates");
	    				}
	    				if(divmonth==7&&divday>31) {
	    					System.out.println("ERROR : US42: " +fam.getId()+fam.getDivorceDate()+": Reject illegitimate dates");
	    				}
	    				if(divmonth==8&&divday>31) {
	    					System.out.println("ERROR : US42: " +fam.getId()+fam.getDivorceDate()+": Reject illegitimate dates");
	    				}
	    				if(divmonth==10&&divday>31) {
	    					System.out.println("ERROR : US42: " +fam.getId()+fam.getDivorceDate()+": Reject illegitimate dates");
	    				}
	    				if(divmonth==12&&divday>31) {
	    					System.out.println("ERROR : US42: " +fam.getId()+fam.getDivorceDate()+": Reject illegitimate dates");
	    				}
	    				divcount = 0;
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
		File file = new File("C:\\Users\\Left丶\\OneDrive - stevens.edu\\桌面\\555\\ssw555-tm5-sprint4_JianfeiLi.ged");
				p.printINDIAndFAMTables(file);
			}
		}
