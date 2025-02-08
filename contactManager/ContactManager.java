package contactManager;

import javax.annotation.processing.FilerException;
import javax.swing.*;
import java.awt.*;
import javax.swing.table.*;
import java.util.*;
import java.io.*;
import java.lang.reflect.Field;
import java.util.List;


public class ContactManager extends JFrame{
  private JTable contactTable;
  private DefaultTableModel tableModel;
  private JTextField nameField, phoneField, emailField;
  private JButton addButton, editButton, deleteButton;
  private List<Contact> contacts;

  //Constructor
  public ContactManager(){
    contacts = new ArrayList<>();
    setTitle("Contact Manager");
    setLayout(new BorderLayout());
    setSize(600, 400);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    //Table setup
    String[] columnName = {"name", "phone", "email"};
    tableModel = new DefaultTableModel(columnName, 0);
    contactTable = new JTable(tableModel);
    JScrollPane tableScrollPane = new JScrollPane(contactTable);
    add(tableScrollPane, BorderLayout.CENTER);

    // form for editing/adding contact
    JPanel formPanel = new JPanel();
    formPanel.setLayout(new GridLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);

    //Name field
    gbc.gridx = 0;
    gbc.gridy = 0;
    formPanel.add(new JLabel("Name:"), gbc);

    nameField = new JTextField(50);
    gbc.gridx = 1;
    formPanel.add(nameField, gbc);

    formPanel.add(new JLabel("Phone: "));
    phoneField = new JTextField();
    formPanel.add(phoneField);

    formPanel.add(new JLabel("Email: "));
    emailField = new JTextField();
    formPanel.add(emailField);

    addButton = new JButton("Add Contact");
    formPanel.add(addButton);

    editButton = new JButton("Edit contact");
    formPanel.add(editButton);

    deleteButton = new JButton("Delete contact");
    formPanel.add(deleteButton);

    add(formPanel, BorderLayout.SOUTH);
   

    //button listeners
    addButton.addActionListener(e -> addContact());
    editButton.addActionListener(e -> editContact());
    deleteButton.addActionListener(e -> deleteContact());
    loadContacts();
    refreshTable();
  }
  private void addContact(){
    String name = nameField.getText();
    String phone = phoneField.getText();
    String email = emailField.getText();

    if (!name.isEmpty() && !phone.isEmpty() & !email.isEmpty()){
      Contact newContact = new Contact (name, phone, email);
      contacts.add(newContact);
      refreshTable();
      loadContacts();
      saveContacts();
      clearForm();
    } else {
      JOptionPane.showMessageDialog(this, "All fields must be filled");
    }
  }

  private void editContact(){
  int selectedRow = contactTable.getSelectedRow();
  if (selectedRow >= 0){
    Contact selecteContact = contacts.get(selectedRow);
    selecteContact.setName(nameField.getText());
    selecteContact.setPhone(phoneField.getText());
    selecteContact.setEmail(emailField.getText());
    refreshTable();
    saveContacts();
  }else {
    JOptionPane.showMessageDialog(this, "Please select a contact to edit!");
  }
  }

  private void deleteContact(){
    int selectedRow = contactTable.getSelectedRow();
    if(selectedRow >= 0){
      contacts.remove(selectedRow);
      refreshTable();
      saveContacts();
    }else {
      JOptionPane.showMessageDialog(this, "Please select a contact to delete!");
    }
  }

  private void refreshTable(){
    tableModel.setRowCount(0);
    for (Contact contact : contacts){
      tableModel.addRow(new Object[]{contact.getName(), contact.getPhone(), contact.getEmail()});
    }
  }

  private void saveContacts(){
    try (BufferedWriter writer = new BufferedWriter(new FileWriter("contacts.txt"))) {
      for (Contact contact : contacts){
        writer.write(contact.getName() + "," +  contact.getPhone() + "," + contact.getEmail());
        writer.newLine();
      }
    } catch (IOException e){
      JOptionPane.showMessageDialog(this, "Error saving contacts.");
    } 
  }


  private void loadContacts(){
    try(BufferedReader reader = new BufferedReader(new FileReader("contacts.txt"))){
      String line;
      while ((line = reader.readLine()) != null){
        String[] data = line.split(",");
        if (data.length == 3){
          contacts.add(new Contact(data[0], data[1], data[2]));
        }
      }
    } catch (IOException e){
      System.out.println("No contact file found, starting an empty list.");
    }
  }


  private void clearForm(){
    nameField.setText("");
    phoneField.setText("");
    emailField.setText("");
  }


  public static void main(String[] args){
   SwingUtilities.invokeLater(() -> {
    ContactManager app = new ContactManager();
    app.setVisible(true);
   });
}
}