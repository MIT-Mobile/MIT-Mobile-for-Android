package edu.mit.mitmobile2.dining;

import java.util.Calendar;      
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import edu.mit.mitmobile2.dining.DiningModel.DailyMeals;
import edu.mit.mitmobile2.dining.DiningModel.HouseDiningHall;
import edu.mit.mitmobile2.dining.DiningModel.Meal;

public class DiningMealIterator {
	
	List<HouseDiningHall> mHalls;
	
	public class MealOrEmptyDay {
		Calendar mDay;
		HashMap<String, Meal> mMeals = new HashMap<String, Meal>();
		
		public MealOrEmptyDay(Calendar day) {
			mDay = day;
		}

		public boolean isEmpty() {
			return (mMeals.size() == 0);
		}		
		
		public List<HouseDiningHall> getHalls() {
			return mHalls;
		}
			
		public Calendar getDay() {
			return mDay;
		}
		
		public Meal getMeal(String hallID) {
			return mMeals.get(hallID);
		}
		
		private Meal getAnyMeal() {
			if (!mMeals.isEmpty()) {
				return mMeals.entrySet().iterator().next().getValue();
			}
			return null;
		}
		
		public String getCapitalizedMealName() {
			Meal meal = getAnyMeal();
			if (meal != null) {
				return meal.getCapitalizedName();
			}
			throw new RuntimeException("No meals to be named");
		}
		
		public String getDayMessage() {
			if (mHalls.size() == 1) {
				DailyMeals meals = mHalls.get(0).getSchedule().getDailyMeals(mDay);
				if (meals != null && meals.getMessage() != null) {
					return meals.getMessage();
				}
			}
			
			// since we can't find or decide which message to use, use a default
			if (isEmpty()) {
				return "No Information Available";
			} else {
				return null;
			}
		}
	}
	
	private MealPointer mCurrentMealPointer;
	private boolean mScheduleUnavailable;
    
    private static class MealPointer {
		private Calendar mDay;
        private String mMealName;  	
        
        public MealPointer(Calendar day, String mealName) {
        	mDay = day;
        	mMealName = mealName;
        }
    }
    
    public DiningMealIterator(Calendar day, List<HouseDiningHall> halls) {
    	mHalls = halls;
    	String mealName = DailyMeals.getFirstMealName();
    	String lastTodayMealName = null;
    	while (mealName != null) {
        	for (HouseDiningHall hall : mHalls) {
        		DailyMeals dailyMeals = hall.getSchedule().getDailyMeals(day);
        		if (dailyMeals != null) {
        			Meal meal = dailyMeals.getMeal(mealName);
        			if (meal != null) {
        				lastTodayMealName = mealName;
        				if (meal.isUpcoming(day) || meal.isInProgress(day)) {
        					// found a meal which is not completed
        					// start here
        					mCurrentMealPointer = new MealPointer(day, mealName);
        					return;
        				}
        			}
        		}
        	}
        	mealName = DailyMeals.getNextMealName(mealName);
    	}
    	
    	mCurrentMealPointer = new MealPointer(day, lastTodayMealName); 
    	mScheduleUnavailable = isAfterAllSchedules(day) || isBeforeAllSchedules(day);
    }
    
    private MealOrEmptyDay getMealOrEmptyDay(Calendar day, String mealName) {
    	MealOrEmptyDay mealOrEmptyDay = new MealOrEmptyDay(day);    	
    	for (HouseDiningHall hall : mHalls) {
    		DailyMeals dailyMeals = hall.getSchedule().getDailyMeals(day);
    		if (dailyMeals != null) {
    			Meal meal = dailyMeals.getMeal(mealName);
    			if (meal != null) {
    				mealOrEmptyDay.mMeals.put(hall.getID(), meal);
    			}
    		}
    	}
    	return mealOrEmptyDay;
    }
    
    public void moveToPrevious() {
    	mCurrentMealPointer = getPointerToPreviousMeal(mCurrentMealPointer);
    }
    
    public void moveToNext() {
    	mCurrentMealPointer = getPointerToNextMeal(mCurrentMealPointer);
    }
    
    public MealOrEmptyDay getCurrent() {
    	return getMealOrEmptyDay(mCurrentMealPointer.mDay, mCurrentMealPointer.mMealName);
    }
    
    public boolean hasNext() {
    	return !mScheduleUnavailable && (getPointerToNextMeal(mCurrentMealPointer) != null);
    }
    
    public MealOrEmptyDay getNext() {
    	MealPointer mealPointer = getPointerToNextMeal(mCurrentMealPointer);
    	return getMealOrEmptyDay(mealPointer.mDay, mealPointer.mMealName);
    }
  
    public boolean hasPrevious() {
    	return !mScheduleUnavailable && (getPointerToPreviousMeal(mCurrentMealPointer) != null);
    }
    
    public MealOrEmptyDay getPrevious() {
    	MealPointer mealPointer = getPointerToPreviousMeal(mCurrentMealPointer);
    	return getMealOrEmptyDay(mealPointer.mDay, mealPointer.mMealName);
    }
  

    
    private MealPointer getPointerToNextMeal(MealPointer mealPointer) {
    	String nextMealName = DailyMeals.getNextMealName(mealPointer.mMealName);
    	MealPointer nextMealPointer = getNextForDay(mealPointer.mDay, nextMealName); 
    	if (nextMealPointer.mMealName != null) {
    		// another meal found today
    		return nextMealPointer;
    	}

    	//  no more meals for current day, check next day
    	Calendar nextDay = new GregorianCalendar();
    	nextDay.setTime(mealPointer.mDay.getTime());
    	nextDay.add(Calendar.DATE, 1);
    	
    	if (isAfterAllSchedules(nextDay)) {
    		return null;
    	}
    	
    	return getNextForDay(nextDay, DailyMeals.getFirstMealName());      	
    }
    
    private MealPointer getNextForDay(Calendar day, String mealName) {    	
		while(mealName != null) {  		
    		MealOrEmptyDay mealOrEmptyDay = getMealOrEmptyDay(day, mealName);
    		if (!mealOrEmptyDay.isEmpty()) {
    			return new MealPointer(day, mealName);
    		}
    		mealName = DailyMeals.getNextMealName(mealName);
    	}  
    	return new MealPointer(day, null);
    }
    
    private boolean isAfterAllSchedules(Calendar day) {
    	for (HouseDiningHall hall : mHalls) {
    		if (!hall.getSchedule().isAfterAllDays(day)) {
    			return false;
    		}
    	}
    	return true;
    }
    

    
    private MealPointer getPointerToPreviousMeal(MealPointer mealPointer) {
    	String previousMealName = DailyMeals.getPreviousMealName(mealPointer.mMealName);
    	MealPointer previousMealPointer = getPreviousForDay(mealPointer.mDay, previousMealName); 
    	if (previousMealPointer.mMealName != null) {
    		// another meal found today
    		return previousMealPointer;
    	}

    	//  no more meals for current day, check next day
    	Calendar previousDay = new GregorianCalendar();
    	previousDay.setTime(mealPointer.mDay.getTime());
    	previousDay.add(Calendar.DATE, -1);
    	
    	if (isBeforeAllSchedules(previousDay)) {
    		return null;
    	}
    	
    	return getPreviousForDay(previousDay, DailyMeals.getLastMealName());      	
    }
    
    private MealPointer getPreviousForDay(Calendar day, String mealName) {    	
		while(mealName != null) {
    		MealOrEmptyDay mealOrEmptyDay = getMealOrEmptyDay(day, mealName);
    		if (!mealOrEmptyDay.isEmpty()) {
    			return new MealPointer(day, mealName);
    		}
    		mealName = DailyMeals.getPreviousMealName(mealName);
    	}  
    	return new MealPointer(day, null);
    }
    
    private boolean isBeforeAllSchedules(Calendar day) {
    	for (HouseDiningHall hall : mHalls) {
    		if (!hall.getSchedule().isBeforeAllDays(day)) {
    			return false;
    		}
    	}
    	return true;
    }	
}
