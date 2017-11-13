package dto;

/**
 * Created by james on 09/11/2017.
 */

public class UserDTO {
    private String email;
    private String ID;


    public UserDTO(){
        //empty constructor
        /*
            from docs https://firebase.google.com/docs/database/android/read-and-write
            If you use a Java object, the contents of your object are
             automatically mapped to child locations in a
             nested fashion.
             Using a Java object also typically makes your code more
              readable and easier to maintain.

         */
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

        public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;}



// private String password;
    //private String name;




//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }

//    public String getID() {
//        return ID;
//    }
//
//    public void setID(String ID) {
//        this.ID = ID;
//    }
}
