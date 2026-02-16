import java.io.*;
import java.util.*;


class Book implements Serializable {
    private String id;
    private String title;
    private String author;
    private boolean isAvailable;

    public Book(String id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isAvailable = true;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    @Override
    public String toString() {
        String status = isAvailable ? "[Available]" : "[Issued]";
        return String.format("ID: %-5s | Title: %-15s | Author: %-15s | %s", id, title, author, status);
    }
}

public class LibrarySystem {
    private static final String FILE_NAME = "library_data.txt";
    private static final String YOUR_SAFETY = "abhi@2005";
    private static List<Book> bookList = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        if (!login()) {
            System.out.println("Too many failed attempts. Closing...");
            return;
        }

        loadFromFile();

        while (true) {
            System.out.println("\n|||||| LIBRARY MENU ||||||");
            System.out.println("1. Add Book\n2. Remove Book\n3. Search Book\n4. View All Books");
            System.out.println("5. Issue Book\n6. Return Book\n7. Save & Exit");
            System.out.print("Choose an option: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1 -> addBook();
                    case 2 -> removeBook();
                    case 3 -> searchBook();
                    case 4 -> viewBooks();
                    case 5 -> issueBook();
                    case 6 -> returnBook();
                    case 7 -> {
                        saveToFile();
                        System.out.println("Data saved");
                        return;
                    }
                    default -> System.out.println("Invalid choice! Enter 1-7.");
                }
            } catch (Exception e) {
                System.out.println("Error: Please enter a valid number.");
            }
        }
    }

    private static boolean login() {
        int attempts = 5;
        while (attempts > 0) {
            System.out.print("Enter Admin Password: ");
            String pass = scanner.nextLine();
            if (pass.equals(YOUR_SAFETY)) {
                System.out.println("Login Successful!\n");
                return true;
            } else {
                attempts--;
                System.out.println("Wrong Password! Attempts left: " + attempts);
            }
        }
        return false;
    }


    private static void addBook() {
        int nextId = bookList.size() + 1;
        String id = scanner.nextLine();
        System.out.print("Enter Title: ");
        String title = scanner.nextLine();
        System.out.print("Enter Author: ");
        String author = scanner.nextLine();
        bookList.add(new Book(id, title, author));
        System.out.println("Book added!");
    }

    private static void removeBook() {
        System.out.print("Enter ID to remove: ");
        String id = scanner.nextLine();
        if (bookList.removeIf(b -> b.getId().equalsIgnoreCase(id))) {
            System.out.println("Book deleted.");
        } else {
            System.out.println("ID not found.");
        }
    }

    private static void searchBook() {
        System.out.print("Enter Bookname to search: ");
        String query = scanner.nextLine().toLowerCase();
        bookList.stream()
                .filter(b -> b.getTitle().toLowerCase().contains(query))
                .forEach(System.out::println);
    }

    private static void viewBooks() {
        if (bookList.isEmpty()) System.out.println("No books in library.");
        else bookList.forEach(System.out::println);
    }

    private static void issueBook() {
        System.out.print("Enter ID to issue: ");
        String id = scanner.nextLine();
        boolean found = false;

        for (Book b : bookList) {
            if (b.getId().equalsIgnoreCase(id)) {
                found = true;
                if (b.isAvailable()) {
                    b.setAvailable(false);
                    System.out.println("Success: book '" + b.getTitle() + "' has been issued.");
                } else {
                    System.out.println("Error: This book is already issued to someone else.");
                }
                return;
            }
        }

        if (!found) {
            System.out.println("Error: No book found with ID [" + id + "]. Please check the ID and try again.");
        }
    }

    private static void returnBook() {
        System.out.print("Enter ID to return: ");
        String id = scanner.nextLine();
        for (Book b : bookList) {
            if (b.getId().equalsIgnoreCase(id) && !b.isAvailable()) {
                b.setAvailable(true);
                System.out.println("Book Returned.");
                return;
            }
        }
        System.out.println("Invalid Return.");
    }


    private static void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(bookList);
        } catch (IOException e) { System.out.println("Save Error."); }
    }

    @SuppressWarnings("unchecked")
    private static void loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            bookList = (List<Book>) ois.readObject();
        } catch (Exception e) {
            try {
                /* New file will be created on save */
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}