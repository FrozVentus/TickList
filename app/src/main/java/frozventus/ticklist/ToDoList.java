package frozventus.ticklist;

public class ToDoList {
    private String title; // title of the "to do"
    private String details;// details of the "to do"
    private boolean isDaily; // marks if the "to do" is to be repeated;
    private String dueDate; // deadline of the "to do" obtained by number of days past 1/1/2000

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

        public boolean setDueDate(String string) {
            dueDate = string;
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
        public String getDueDate() {
            return dueDate;
        }
}
