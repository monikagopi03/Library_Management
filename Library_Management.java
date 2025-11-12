import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * A single-file, runnable Library Management System.
 * Includes classes: Book, Member, Library, and the main UI.
 */
public class LibraryManagementSystem {

    public static void main(String[] args) {
        // Initialize the main library and a scanner for UI
        Library library = new Library();
        Scanner scanner = new Scanner(System.in);
        
        // Pre-populate with some data
        library.addBook("978-0321765723", "The Lord of the Rings", "J.R.R. Tolkien");
        library.addBook("978-0743273565", "The Great Gatsby", "F. Scott Fitzgerald");
        library.addBook("978-0451524935", "1984", "George Orwell");
        library.registerMember("M001", "Alice Smith");
        library.registerMember("M002", "Bob Johnson");

        System.out.println("Welcome to the Library Management System!");
        int choice = -1;

        // Main application loop
        while (choice != 0) {
            printMenu();
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        library.listAvailableBooks();
                        break;
                    case 2:
                        System.out.print("Enter Member ID: ");
                        String memberIdOut = scanner.nextLine();
                        System.out.print("Enter Book ISBN: ");
                        String isbnOut = scanner.nextLine();
                        library.checkOutBook(isbnOut, memberIdOut);
                        break;
                    case 3:
                        System.out.print("Enter Member ID: ");
                        String memberIdIn = scanner.nextLine();
                        System.out.print("Enter Book ISBN: ");
                        String isbnIn = scanner.nextLine();
                        library.returnBook(isbnIn, memberIdIn);
                        break;
                    case 4:
                        System.out.print("Enter Member ID to list loans: ");
                        library.listMemberLoans(scanner.nextLine());
                        break;
                    case 5:
                        System.out.print("Enter new Book ISBN: ");
                        String newIsbn = scanner.nextLine();
                        System.out.print("Enter Book Title: ");
                        String newTitle = scanner.nextLine();
                        System.out.print("Enter Book Author: ");
                        String newAuthor = scanner.nextLine();
                        library.addBook(newIsbn, newTitle, newAuthor);
                        break;
                    case 6:
                        System.out.print("Enter new Member ID: ");
                        String newMemberId = scanner.nextLine();
                        System.out.print("Enter Member Name: ");
                        String newName = scanner.nextLine();
                        library.registerMember(newMemberId, newName);
                        break;
                    case 0:
                        System.out.println("Exiting system. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear the bad input
            }
            System.out.println(); // Add a space for readability
        }
        scanner.close();
    }

    /**
     * Prints the main user menu.
     */
    private static void printMenu() {
        System.out.println("--- Library Menu ---");
        System.out.println("1. List Available Books");
        System.out.println("2. Check Out Book");
        System.out.println("3. Return Book");
        System.out.println("4. List Member's Loans");
        System.out.println("5. Add New Book");
        System.out.println("6. Register New Member");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }
}

/**
 * Represents a single book in the library.
 */
class Book {
    private String isbn;
    private String title;
    private String author;
    private boolean isAvailable;
    private LocalDate dueDate;

    public Book(String isbn, String title, String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.isAvailable = true;
        this.dueDate = null;
    }

    // --- Getters ---
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public boolean isAvailable() { return isAvailable; }
    public LocalDate getDueDate() { return dueDate; }

    // --- Core Methods ---
    public void checkOut(LocalDate dueDate) {
        this.isAvailable = false;
        this.dueDate = dueDate;
    }

    public void returnBook() {
        this.isAvailable = true;
        this.dueDate = null;
    }

    @Override
    public String toString() {
        String status = isAvailable ? "Available" : "Checked Out (Due: " + dueDate + ")";
        return "Book [ISBN: " + isbn + ", Title: '" + title + 
               "', Author: '" + author + "', Status: " + status + "]";
    }
}

/**
 * Represents a library member.
 */
class Member {
    private String memberId;
    private String name;
    private List<Book> borrowedBooks;

    public Member(String memberId, String name) {
        this.memberId = memberId;
        this.name = name;
        this.borrowedBooks = new ArrayList<>();
    }

    // --- Getters ---
    public String getMemberId() { return memberId; }
    public String getName() { return name; }
    public List<Book> getBorrowedBooks() { return borrowedBooks; }

    // --- Core Methods ---
    public void borrowBook(Book book) {
        if (book != null && !borrowedBooks.contains(book)) {
            borrowedBooks.add(book);
        }
    }

    public void returnBook(Book book) {
        if (book != null) {
            borrowedBooks.remove(book);
        }
    }

    @Override
    public String toString() {
        return "Member [ID: " + memberId + ", Name: '" + name + 
               "', Books Borrowed: " + borrowedBooks.size() + "]";
    }
}

/**
 * The main library "engine" that manages data and logic.
 */
class Library {
    // Constants for loan rules
    private static final int LOAN_PERIOD_DAYS = 14;
    private static final double FINE_PER_DAY = 0.50; // 50 cents per day

    // Data storage
    private Map<String, Book> books;
    private Map<String, Member> members;

    public Library() {
        this.books = new HashMap<>();
        this.members = new HashMap<>();
    }

    // --- Admin Methods ---
    public void addBook(String isbn, String title, String author) {
        if (books.containsKey(isbn)) {
            System.out.println("Error: Book with this ISBN already exists.");
        } else {
            Book newBook = new Book(isbn, title, author);
            books.put(isbn, newBook);
            System.out.println("Book added: " + newBook.getTitle());
        }
    }

    public void registerMember(String memberId, String name) {
        if (members.containsKey(memberId)) {
            System.out.println("Error: Member with this ID already exists.");
        } else {
            Member newMember = new Member(memberId, name);
            members.put(memberId, newMember);
            System.out.println("Member registered: " + newMember.getName());
        }
    }

    // --- Core Transaction Methods ---
    public void checkOutBook(String isbn, String memberId) {
        Book book = books.get(isbn);
        Member member = members.get(memberId);

        if (book == null) {
            System.out.println("Error: Book not found.");
            return;
        }
        if (member == null) {
            System.out.println("Error: Member not found.");
            return;
        }

        if (book.isAvailable()) {
            LocalDate dueDate = LocalDate.now().plusDays(LOAN_PERIOD_DAYS);
            book.checkOut(dueDate);
            member.borrowBook(book);
            System.out.println("Success: " + member.getName() + 
                               " checked out '" + book.getTitle() + "'.");
            System.out.println("Due Date: " + dueDate);
        } else {
            System.out.println("Error: Book is already checked out.");
        }
    }

    public void returnBook(String isbn, String memberId) {
        Book book = books.get(isbn);
        Member member = members.get(memberId);

        if (book == null) {
            System.out.println("Error: Book not found.");
            return;
        }
        if (member == null) {
            System.out.println("Error: Member not found.");
            return;
        }

        if (!member.getBorrowedBooks().contains(book)) {
            System.out.println("Error: This member did not borrow this book.");
            return;
        }

        // Calculate fine *before* returning
        double fine = calculateFine(book);
        if (fine > 0) {
            System.out.printf("Book is overdue! Fine: $%.2f%n", fine);
        } else {
            System.out.println("Book returned on time.");
        }

        book.returnBook();
        member.returnBook(book);
        System.out.println("Success: " + member.getName() + 
                           " returned '" + book.getTitle() + "'.");
    }

    private double calculateFine(Book book) {
        if (book.getDueDate() != null && LocalDate.now().isAfter(book.getDueDate())) {
            long daysOverdue = ChronoUnit.DAYS.between(book.getDueDate(), LocalDate.now());
            return daysOverdue * FINE_PER_DAY;
        }
        return 0.0; // No fine
    }

    // --- Reporting Methods ---
    public void listAvailableBooks() {
        System.out.println("--- Available Books ---");
        boolean found = false;
        for (Book book : books.values()) {
            if (book.isAvailable()) {
                System.out.println(book);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No books are currently available.");
        }
    }

    public void listMemberLoans(String memberId) {
        Member member = members.get(memberId);
        if (member == null) {
            System.out.println("Error: Member not found.");
            return;
        }

        System.out.println("--- Loans for " + member.getName() + " ---");
        List<Book> loans = member.getBorrowedBooks();
        if (loans.isEmpty()) {
            System.out.println("This member has no books checked out.");
        } else {
            for (Book book : loans) {
                System.out.println(book);
                // Show current fine if any
                double fine = calculateFine(book);
                if (fine > 0) {
                    System.out.printf("  -> Current Fine: $%.2f%n", fine);
                }
            }
        }
    }
}