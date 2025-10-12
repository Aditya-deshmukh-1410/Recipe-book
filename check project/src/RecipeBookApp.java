import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.*;

public class RecipeBookApp {
    private JTextField searchField;
    private JLabel nameLabel, ingredientLabel, durationLabel, processLabel, photoLabel;

    public RecipeBookApp() {
        JFrame jFrame = new JFrame();
        jFrame.setLayout(null);
        jFrame.setSize(800, 800);
        jFrame.setVisible(true);
        jFrame.setTitle("Recipe Book");
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ImageIcon icon = new ImageIcon("C:/Users/a2az/Downloads/book1.png");
        jFrame.setIconImage(icon.getImage());

        JLabel jLabel = new JLabel("Search for Recipe");
        jLabel.setBounds(360, 40, 400, 60);
        jFrame.add(jLabel);

        searchField = new JTextField("");
        searchField.setBounds(150, 100, 500, 30);
        jFrame.add(searchField);

        nameLabel = new JLabel("Name: ");
        nameLabel.setBounds(160, 370, 500, 30);
        jFrame.add(nameLabel);

        ingredientLabel = new JLabel("Ingredients: ");
        ingredientLabel.setBounds(160, 400, 500, 30);
        jFrame.add(ingredientLabel);

        durationLabel = new JLabel("Duration: ");
        durationLabel.setBounds(160, 430, 500, 30);
        jFrame.add(durationLabel);

        processLabel = new JLabel("Process: ");
        processLabel.setBounds(160, 460, 500, 30);
        jFrame.add(processLabel);

        photoLabel = new JLabel();
        photoLabel.setBounds(300, 160, 200, 200);
        jFrame.add(photoLabel);


        JList<String> recipeList=new JList<String>();

        DefaultListModel<String> listModel=new DefaultListModel<String>();

        JScrollPane scrollPane = new JScrollPane(recipeList);
        scrollPane.setBounds(100, 150, 600, 500);
        jFrame.add(scrollPane);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                searchRecipe(searchField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                searchRecipe(searchField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                searchRecipe(searchField.getText());
            }
        });
    }

    private void searchRecipe(String recipeName) {
        String url = "jdbc:mysql://localhost:3306/recipe_book";
        String username = "root";
        String password = "";

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String sql = "SELECT name, ingredients, duration, process, photo FROM recipes WHERE name LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + recipeName + "%");

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                String ingredients = rs.getString("ingredients");
                String duration = rs.getString("duration");
                String process = rs.getString("process");
                Blob photoBlob = rs.getBlob("photo");

                nameLabel.setText("Name: " + name);
                ingredientLabel.setText("Ingredients: " + ingredients);
                durationLabel.setText("Duration: " + duration);
                processLabel.setText("Process: " + process);

                if (photoBlob != null) {
                    byte[] photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());
                    ByteArrayInputStream bis = new ByteArrayInputStream(photoBytes);
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    int nRead;
                    byte[] data = new byte[1024];
                    while ((nRead = bis.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, nRead);
                    }
                    buffer.flush();
                    byte[] finalPhotoBytes = buffer.toByteArray();
                    ImageIcon imageIcon = new ImageIcon(finalPhotoBytes);
                    Image image = imageIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                    photoLabel.setIcon(new ImageIcon(image));
                } else {
                    photoLabel.setIcon(null);
                }
            } else {

                JOptionPane.showMessageDialog(null, "Recipe not found.", "Not Found", JOptionPane.INFORMATION_MESSAGE);


                nameLabel.setText("Name: ");
                ingredientLabel.setText("Ingredients: ");
                durationLabel.setText("Duration: ");
                processLabel.setText("Process: ");
                photoLabel.setIcon(null);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new RecipeBookApp();
    }
}

