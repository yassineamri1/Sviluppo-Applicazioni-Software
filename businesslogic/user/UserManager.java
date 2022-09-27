package businesslogic.user;

public class UserManager {
    private User currentUser;

    public void logIn(String username) {
        this.currentUser = User.loadUser(username);
    }

    public void logIn(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return this.currentUser;
    }

    public void logOut() {
        this.currentUser = null;
    }
}
