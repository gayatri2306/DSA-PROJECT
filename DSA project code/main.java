import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

// 1. Product Class
class Product {
    private int productId;
    private String name;
    private int quantity;
    private double price;

    public Product(int productId, String name, int quantity, double price) {
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String toFileString() {
        String safeName = name.replace(",", " ");
        return productId + "," + safeName + "," + quantity + "," + price;
    }

    public static Product fromFileString(String line) {
        String[] parts = line.split(",");
        if (parts.length != 4) return null;
        try {
            int id = Integer.parseInt(parts[0].trim());
            String name = parts[1].trim();
            int qty = Integer.parseInt(parts[2].trim());
            double price = Double.parseDouble(parts[3].trim());
            return new Product(id, name, qty, price);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("%-10d | %-20s | %-10d | %-10.2f", productId, name, quantity, price);
    }
}

// 2. FileManager Class
class FileManager {
    private final String filePath;

    public FileManager(String filePath) {
        this.filePath = filePath;
    }

    public boolean saveToFile(List<Product> products) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Product product : products) {
                writer.write(product.toFileString());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error saving inventory: " + e.getMessage());
            return false;
        }
    }

    public List<Product> loadFromFile() {
        List<Product> products = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return products;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Product product = Product.fromFileString(line);
                if (product != null) {
                    products.add(product);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading inventory: " + e.getMessage());
        }
        return products;
    }
}

// 3. InventoryManager Class
class InventoryManager {
    private final List<Product> products;
    private final FileManager fileManager;

    public InventoryManager(String filePath) {
        this.fileManager = new FileManager(filePath);
        this.products = new ArrayList<>(fileManager.loadFromFile());
    }

    public boolean addProduct(Product product) {
        if (findIndexById(product.getProductId()) != -1) {
            return false;
        }
        products.add(product);
        return true;
    }

    public void displayProducts() {
        if (products.isEmpty()) {
            System.out.println("\nInventory is currently empty.");
            return;
        }
        System.out.println("\n----------------------------------------------------------------");
        System.out.printf("%-10s | %-20s | %-10s | %-10s%n", "Product ID", "Product Name", "Quantity", "Price");
        System.out.println("----------------------------------------------------------------");
        for (Product product : products) {
            System.out.println(product);
        }
        System.out.println("----------------------------------------------------------------");
    }

    public Product searchById(int id) {
        for (Product product : products) {
            if (product.getProductId() == id) return product;
        }
        return null;
    }

    public List<Product> searchByName(String name) {
        List<Product> matches = new ArrayList<>();
        String query = name.trim().toLowerCase();
        for (Product product : products) {
            if (product.getName().toLowerCase().contains(query)) {
                matches.add(product);
            }
        }
        return matches;
    }

    public boolean updateProduct(int id, String newName, int newQuantity, double newPrice) {
        Product product = searchById(id);
        if (product == null) return false;
        product.setName(newName);
        product.setQuantity(newQuantity);
        product.setPrice(newPrice);
        return true;
    }

    public boolean deleteProduct(int id) {
        int index = findIndexById(id);
        if (index == -1) return false;
        products.remove(index);
        return true;
    }

    public void sortById() {
        products.sort(Comparator.comparingInt(Product::getProductId));
    }

    public void sortByName() {
        products.sort(Comparator.comparing(p -> p.getName().toLowerCase()));
    }

    public void sortByQuantity() {
        products.sort(Comparator.comparingInt(Product::getQuantity));
    }

    public boolean saveInventory() {
        return fileManager.saveToFile(products);
    }

    private int findIndexById(int id) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getProductId() == id) return i;
        }
        return -1;
    }
}

// 4. Main Class (matches main.java)
public class main {
    private static final String DATA_FILE = "inventory.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        InventoryManager manager = new InventoryManager(DATA_FILE);

        boolean running = true;
        while (running) {
            System.out.println("\n===== SIMPLE INVENTORY TRACKER =====");
            System.out.println("1. Add Product");
            System.out.println("2. Display Products");
            System.out.println("3. Search Product");
            System.out.println("4. Update Product");
            System.out.println("5. Delete Product");
            System.out.println("6. Sort Products");
            System.out.println("7. Save Inventory");
            System.out.println("8. Exit");

            int choice = readInt(scanner, "Enter your choice: ");

            switch (choice) {
                case 1 -> {
                    int id = readInt(scanner, "Enter Product ID: ");
                    System.out.print("Enter Product Name: ");
                    String name = scanner.nextLine().trim();
                    int quantity = readInt(scanner, "Enter Quantity: ");
                    double price = readDouble(scanner, "Enter Price: ");

                    if (name.isEmpty() || quantity < 0 || price < 0) {
                        System.out.println("Invalid input! Name cannot be empty, and quantity/price cannot be negative.");
                    } else if (manager.addProduct(new Product(id, name, quantity, price))) {
                        System.out.println("Product added successfully!");
                    } else {
                        System.out.println("Error: Product ID " + id + " already exists.");
                    }
                }
                case 2 -> manager.displayProducts();
                case 3 -> {
                    System.out.println("Search by: 1. Product ID  |  2. Product Name");
                    int option = readInt(scanner, "Enter option: ");
                    if (option == 1) {
                        int id = readInt(scanner, "Enter Product ID: ");
                        Product found = manager.searchById(id);
                        if (found != null) {
                            System.out.printf("%-10s | %-20s | %-10s | %-10s%n", "Product ID", "Product Name", "Quantity", "Price");
                            System.out.println(found);
                        } else {
                            System.out.println("Product Not Found.");
                        }
                    } else if (option == 2) {
                        System.out.print("Enter Product Name: ");
                        String name = scanner.nextLine().trim();
                        List<Product> matches = manager.searchByName(name);
                        if (!matches.isEmpty()) {
                            System.out.printf("%-10s | %-20s | %-10s | %-10s%n", "Product ID", "Product Name", "Quantity", "Price");
                            for (Product p : matches) System.out.println(p);
                        } else {
                            System.out.println("Product Not Found.");
                        }
                    } else {
                        System.out.println("Invalid option.");
                    }
                }
                case 4 -> {
                    int id = readInt(scanner, "Enter Product ID to update: ");
                    if (manager.searchById(id) == null) {
                        System.out.println("Product Not Found.");
                    } else {
                        System.out.print("Enter New Product Name: ");
                        String newName = scanner.nextLine().trim();
                        int newQuantity = readInt(scanner, "Enter New Quantity: ");
                        double newPrice = readDouble(scanner, "Enter New Price: ");
                        if (!newName.isEmpty() && newQuantity >= 0 && newPrice >= 0 && manager.updateProduct(id, newName, newQuantity, newPrice)) {
                            System.out.println("Product updated successfully!");
                        } else {
                            System.out.println("Invalid input! Update cancelled.");
                        }
                    }
                }
                case 5 -> {
                    int id = readInt(scanner, "Enter Product ID to delete: ");
                    if (manager.deleteProduct(id)) {
                        System.out.println("Product deleted successfully!");
                    } else {
                        System.out.println("Product Not Found.");
                    }
                }
                case 6 -> {
                    System.out.println("Sort by: 1. Product ID  |  2. Product Name  |  3. Quantity");
                    int option = readInt(scanner, "Enter option: ");
                    if (option == 1) {
                        manager.sortById();
                        System.out.println("Sorted by Product ID.");
                        manager.displayProducts();
                    } else if (option == 2) {
                        manager.sortByName();
                        System.out.println("Sorted by Product Name.");
                        manager.displayProducts();
                    } else if (option == 3) {
                        manager.sortByQuantity();
                        System.out.println("Sorted by Quantity.");
                        manager.displayProducts();
                    } else {
                        System.out.println("Invalid option.");
                    }
                }
                case 7 -> {
                    if (manager.saveInventory()) {
                        System.out.println("Inventory saved successfully to " + DATA_FILE + "!");
                    }
                }
                case 8 -> {
                    manager.saveInventory();
                    System.out.println("Inventory saved. Exiting... Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid choice! Enter 1-8.");
            }
        }
        scanner.close();
    }

    private static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static double readDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid price.");
            }
        }
    }
}