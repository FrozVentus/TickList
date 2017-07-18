package frozventus.ticklist;

public class ToDoList {
    private String title; // title of the "to do"
    private String details;// details of the "to do"
    private boolean isDaily; // marks if the "to do" is to be repeated;
    private int dueDate; // due date
    private int dueMonth;
    private int dueYear;

        //newly added constructors
        public ToDoList() {
            setTitle("");
            setDetails("");
        }

        public ToDoList(String taskName, String details) {
            setTitle(taskName);
            setDetails(details);
        }

        public boolean setTitle(String string) {
            if(string.equals(""))
                return false;
            else
                title = string;
            return true;
        }

        public boolean setDetails(String string) {
            details = string;
            return true;
        }

        public boolean setDaily(Boolean daily) {
            isDaily = daily;
            return true;
        }

        public boolean setDue(int date, int month, int year) {
            dueDate = date;
            dueMonth = month;
            dueYear = year;
            return true;
        }

        public String getTitle() {
            return title;
        }
        public String getDeatils() {
            return details;
        }
        public boolean isDaily() {
            return isDaily;
        }
        public String getDue() {
            return dueDate + "/" + dueMonth + "/" + dueYear;
        }//dd/mm/yyyy
        public int getDueDate() {
            return dueDate;
        }
        public int getDueMonth() {
            return dueMonth;
        }
        public int getDueYear() {
            return dueYear;
        }
}
