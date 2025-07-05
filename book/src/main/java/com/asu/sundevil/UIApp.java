package com.asu.sundevil;

import javafx.application.Application;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import java.util.function.UnaryOperator;
import javafx.beans.property.SimpleStringProperty;
import com.asu.sundevil.model.BookListing;
import com.asu.sundevil.model.Book;
import com.asu.sundevil.model.User;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import com.asu.sundevil.model.Transaction;







import java.time.LocalDateTime;
import java.util.UUID;

public class UIApp extends Application {
    private Stage stage;
    private Scene loginScene, buyerScene, sellerScene, adminScene;
    private User  currentUser;
    private PriceSuggestionEngine engine  = new PriceSuggestionEngine();
    private NotificationService notifier  = new NotificationService();

    @Override
    public void start(Stage primaryStage) {
        DataService.init();
        this.stage = primaryStage;
        buildLoginScene();
        primaryStage.setTitle("SunDevil Marketplace");
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    private void buildLoginScene() {
        // Labels and inputs
        Label userLabel = new Label("Username:");
        TextField userField = new TextField();

        Label passLabel = new Label("Password:");
        PasswordField passField = new PasswordField();

        Label roleLabel = new Label("Role:");
        ComboBox<String> roleBox = new ComboBox<>(
                FXCollections.observableArrayList("Buyer", "Seller", "Admin")
        );
        roleBox.setValue("Buyer");  // Buyer

        Button loginBtn = new Button("Login");
        Label msgLabel = new Label();

        // Logic for login button: accepts any username/password, routed only by role
        loginBtn.setOnAction(e -> {
            msgLabel.setText("");

            String username = userField.getText().trim();
            String role     = roleBox.getValue();

            if (username.isEmpty()) {
                msgLabel.setText("Please enter a username");
                return;
            }

            // No more user name or password validation
            currentUser = new User(username, role);

            // Switch scenes according to the selected character
            switch (role) {
                case "Buyer":
                    buildBuyerScene();
                    stage.setScene(buyerScene);
                    break;
                case "Seller":
                    buildSellerScene();
                    stage.setScene(sellerScene);
                    break;
                case "Admin":
                    buildAdminScene();
                    stage.setScene(adminScene);
                    break;
            }
        });

        // composition
        VBox layout = new VBox(10,
                userLabel, userField,
                passLabel, passField,
                roleLabel, roleBox,
                loginBtn, msgLabel
        );
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        loginScene = new Scene(layout, 400, 300);
    }


    private void buildBuyerScene() {
        // --- TableView setup ---
        TableView<BookListing> tv = new TableView<>();
        tv.setItems(FXCollections.observableList(DataService.listings));

        // ISBN column
        TableColumn<BookListing, String> isbnCol = new TableColumn<>("ISBN");
        isbnCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getBook().getIsbn())
        );

        // Title column
        TableColumn<BookListing, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getBook().getTitle())
        );

        // Author column
        TableColumn<BookListing, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getBook().getAuthor())
        );

        // Price column (2-decimal)
        TableColumn<BookListing, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        String.format("%.2f", cell.getValue().getPrice())
                )
        );

        // Status column
        TableColumn<BookListing, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getStatus())
        );

        tv.getColumns().setAll(isbnCol, titleCol, authorCol, priceCol, statusCol);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // --- Controls ---
        Button refresh = new Button("Refresh");
        refresh.setOnAction(e -> tv.setItems(FXCollections.observableList(DataService.listings)));

        Button buy = new Button("Purchase Selected");
        buy.setOnAction(e -> {
            BookListing sel = tv.getSelectionModel().getSelectedItem();
            if (sel != null && "ACTIVE".equals(sel.getStatus())) {
                sel.setStatus("RESERVED");
                Transaction tx = new Transaction(
                        UUID.randomUUID().toString(),
                        sel.getListingId(),
                        currentUser.getUsername(),
                        LocalDateTime.now(),
                        "RESERVED"
                );
                DataService.transactions.add(tx);
                notifier.sendEmail(currentUser, "Purchase Confirmed", sel.getBook().getTitle());
                // notify seller...
                DataService.saveAll();
                tv.refresh();
            }
        });

        Button logout = new Button("Logout");
        logout.setOnAction(e -> {
            DataService.saveAll();
            stage.setScene(loginScene);
        });

        HBox controls = new HBox(15, refresh, buy, logout);
        controls.setAlignment(Pos.CENTER);

        VBox root = new VBox(20, new Label("Buyer Portal"), tv, controls);
        root.setPadding(new Insets(20));
        buyerScene = new Scene(root, 650, 450);
    }



    private void buildSellerScene() {
        // Logout
        Button logoutBtn = new Button("Logout");
        logoutBtn.setOnAction(e -> stage.setScene(loginScene));

        // Form controls
        Label isbnLabel   = new Label("ISBN:");
        TextField isbnF   = new TextField();

        Label titleLabel  = new Label("Title:");
        TextField titleF  = new TextField();

        Label authorLabel = new Label("Author:");
        TextField authorF = new TextField();

        Label priceLabel  = new Label("Price:");
        TextField priceF  = new TextField();
        UnaryOperator<TextFormatter.Change> priceFilter = change -> {
            String t = change.getControlNewText();
            return t.matches("\\d*(\\.\\d{0,2})?") ? change : null;
        };
        priceF.setTextFormatter(new TextFormatter<>(priceFilter));

        Button addBtn = new Button("Add Listing");
        Label msgLabel = new Label();

        // TableView
        TableView<BookListing> table = new TableView<>();
        table.setItems(FXCollections.observableArrayList(DataService.listings));

        TableColumn<BookListing, String> isbnCol = new TableColumn<>("ISBN");
        isbnCol.setCellValueFactory(r ->
                new SimpleStringProperty(r.getValue().getBook().getIsbn())
        );

        TableColumn<BookListing, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(r ->
                new SimpleStringProperty(r.getValue().getBook().getTitle())
        );

        TableColumn<BookListing, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(r ->
                new SimpleStringProperty(r.getValue().getBook().getAuthor())
        );

        TableColumn<BookListing, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(r ->
                new SimpleStringProperty(
                        String.format("%.2f", r.getValue().getPrice())
                )
        );

        table.getColumns().setAll(isbnCol, titleCol, authorCol, priceCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Listing
        addBtn.setOnAction(e -> {
            String isbn   = isbnF.getText().trim();
            String title  = titleF.getText().trim();
            String author = authorF.getText().trim();
            String ptext  = priceF.getText().trim();

            if (isbn.isEmpty() || title.isEmpty() || author.isEmpty() || ptext.isEmpty()) {
                msgLabel.setText("Please fill in all fields");
                return;
            }
            double price;
            try { price = Double.parseDouble(ptext); }
            catch (NumberFormatException ex) {
                msgLabel.setText("Invalid price");
                return;
            }

            Book book = new Book(isbn, title, author);
            BookListing listing = new BookListing(
                    isbn,
                    book,
                    price,
                    "ACTIVE",
                    currentUser.getUsername(),
                    ""
            );
            DataService.listings.add(listing);
            table.getItems().add(listing);

            isbnF.clear();
            titleF.clear();
            authorF.clear();
            priceF.clear();
            msgLabel.setText("Listing added!");
        });

        // Layout combinations
        HBox topBar = new HBox(logoutBtn);
        topBar.setAlignment(Pos.TOP_RIGHT);
        topBar.setPadding(new Insets(10));

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.add(isbnLabel,   0, 0); form.add(isbnF,   1, 0);
        form.add(titleLabel,  0, 1); form.add(titleF,  1, 1);
        form.add(authorLabel, 0, 2); form.add(authorF, 1, 2);
        form.add(priceLabel,  0, 3); form.add(priceF,  1, 3);
        form.add(addBtn,      0, 4); form.add(msgLabel, 1, 4);

        VBox root = new VBox(15, topBar, form, table);
        root.setPadding(new Insets(20));

        sellerScene = new Scene(root, 700, 450);
    }



    private void buildAdminScene() {
        Label totalL = new Label(), usersL = new Label(), salesL = new Label();

        Button refresh = new Button("Refresh");
        refresh.setOnAction(e-> {
            totalL.setText("Total Listings: " + DataService.listings.size());
            usersL.setText("Users: " + DataService.users.size());
            long ws = DataService.transactions.stream()
              .filter(t->t.getTimestamp().isAfter(LocalDateTime.now().minusDays(7)))
              .count();
            salesL.setText("Weekly Sales: " + ws);
        });
        refresh.fire();

        TableView<Transaction> tv = new TableView<>();
        tv.setItems(FXCollections.observableList(DataService.transactions));
        tv.getColumns().addAll(
          column("Txn ID",    "transactionId"),
          column("ListingID", "listingId"),
          column("Buyer",     "buyer"),
          column("Time",      "timestamp"),
          column("Status",    "status")
        );

        Button logout = new Button("Logout");
        logout.setOnAction(e-> { DataService.saveAll(); stage.setScene(loginScene); });

        VBox layout = new VBox(20,
          new Label("Admin Dashboard"),
          totalL, usersL, salesL,
          refresh,
          new Label("Transactions"), tv,
          logout
        );
        layout.setPadding(new Insets(20));
        adminScene = new Scene(layout, 750, 650);
    }

    private <S,T> TableColumn<S,T> column(String header, String prop) {
        TableColumn<S,T> col = new TableColumn<>(header);
        col.setCellValueFactory(new PropertyValueFactory<>(prop));
        return col;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
