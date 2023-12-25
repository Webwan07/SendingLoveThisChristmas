package myclass;

import java.awt.Component;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Database {
    private final static String url = "jdbc:mysql://localhost:3306/mydb?zeroDateTimeBehavior=CONVERT_TO_NULL";
    private final static String username = "root";
    private final static String password = "";    
    
    public static Connection connection;
    public static Statement statement;
    public static PreparedStatement prepare;   
    public static ResultSet result;
    public static ResultSetMetaData metaData;
    
    public final static String inventoryTable = "inventorytable";
    public final static String[] inventoryColumns = {"productID","Category","ProductName",
        "Description","Quantity","RetailPrice","DateOfPurchase"};
    
    private final static String categoryTable = "categorytable";
    private final static String[] categoryColumns = {"categoryID","categoryName","dateCreated"};
    
    private final static String appTable = "apptable";
    private final static String[] appColumns = {"appID","countUsers","currentUser"};
    
    private final static String usersTable = "userstable";
    private final static String[] usersColumns = {"userId","firstname","lastname","username","password",
        "birthdate","gender","profileImgPath","userType"};
    
    private final static String recordsTable = "recordstable";
    private final static String[] recordsColumns = {"recordDate","sold"};
    
    private final Component component;
    
    public Database(Component component){
        this.component = component;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(url, username, password);
            //this.connection = DriverManager.getConnection(urlOnline, usernameOnline, passwordOnline);
            this.statement = connection.createStatement();
        }catch(Exception e){
          
        }              
    }
    
    public boolean isDatabaseConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    //Inventory Section
    public DefaultTableModel DisplayInventoryData(){
        String query = "SELECT * FROM " + inventoryTable;

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{"Product ID","Category","Product Name","Description","Quantity","Retail Price","Date of Purchase"});
        try {
            result = statement.executeQuery(query);
            metaData = result.getMetaData();
            int numberOfColumns = metaData.getColumnCount();

            while (result.next()) {
                Object[] rowData = new Object[numberOfColumns];
                for (int i = 1; i <= numberOfColumns; i++) {
                    rowData[i - 1] = result.getObject(i);
                }
                model.addRow(rowData);
            }

            result.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, "", "", JOptionPane.ERROR_MESSAGE);
        }
        return model;
    }   
    
    public void addInventoryValue(Object[] values){
        String query = "INSERT INTO "+inventoryTable+
                " ("+inventoryColumns[1]+","+inventoryColumns[2]+","+inventoryColumns[3]+
                ","+inventoryColumns[4]+","+inventoryColumns[5]+","+inventoryColumns[6]+") VALUES (?,?,?,?,?,?)";
        
        try {
            prepare = connection.prepareStatement(query);
            prepare.setObject(1, values[0]);
            prepare.setObject(2, values[1]);
            prepare.setObject(3, values[2]); 
            prepare.setObject(4, values[3]); 
            prepare.setObject(5, values[4]); 
            prepare.setObject(6, values[5]);

            prepare.executeUpdate();
            prepare.close();
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(component, e.getMessage(), e.getErrorCode()+"", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void EditInventoryValue(Object minuVal, int colIdx, Object id){
        String query = "UPDATE " + inventoryTable + " SET " + inventoryColumns[colIdx] + " = ? WHERE " + inventoryColumns[0] + " = ?";

        try {
            prepare = connection.prepareStatement(query);
            prepare.setObject(1, minuVal);
            prepare.setObject(2, id);

            prepare.executeUpdate();
            prepare.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, "", "", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void DeleteInventoryRecord(Object ID) {
        String query = "DELETE FROM "+inventoryTable+" WHERE "+inventoryColumns[0]+" = ?";
        
        try{
            prepare = connection.prepareStatement(query);
            prepare.setObject(1, ID);
            
            prepare.executeUpdate();
            prepare.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(component, "", "", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void loadInventoryData(JTable table, String search){
        String query = "SELECT * FROM "+inventoryTable+" WHERE "
                    + String.join(" LIKE ? OR ", inventoryColumns) + " LIKE ?";    
        try{
            prepare = connection.prepareStatement(query);
            for (int i = 1; i <= inventoryColumns.length; i++) {
                prepare.setString(i, "%" + search + "%");
            }            
            
            result = prepare.executeQuery();
            DefaultTableModel model = (javax.swing.table.DefaultTableModel) table.getModel();
            model.setRowCount(0);            
            
            while (result.next()) {
                Object[] row = new Object[inventoryColumns.length];
                for (int i = 0; i < inventoryColumns.length; i++) {
                    row[i] = result.getObject(inventoryColumns[i]);
                }
                model.addRow(row);
            }
            
            result.close();
            prepare.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(component, e.getMessage(), e.getSQLState(), JOptionPane.ERROR_MESSAGE);
        }        
    }
    
    public void reduceProductQuantity(Object minusVal, Object p_n){
        String query = "UPDATE "+inventoryTable+" SET "+inventoryColumns[4]+
                " = "+inventoryColumns[4]+" - ? WHERE "+inventoryColumns[2]+" = ?";

        try {
            prepare = connection.prepareStatement(query);
            prepare.setObject(1, minusVal);
            prepare.setObject(2, p_n);

            prepare.executeUpdate();
            prepare.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, "Error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //Category
    public String[] AddElementToComboBox(){
        String query = "SELECT " + categoryColumns[1] + " FROM " + categoryTable;
        
        List<String> getVal = new ArrayList<>();
        
        try {
            result = statement.executeQuery(query);
            while (result.next()) {
                getVal.add(result.getString(categoryColumns[1]));
            }
            result.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, "", "", JOptionPane.ERROR_MESSAGE);
        }
        return getVal.toArray(new String[0]);
    }
    
    public DefaultTableModel DisplayCategoryData(){
        String query = "SELECT * FROM " + categoryTable;

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{"Category ID","Category Name","Date Added"});
        try {
            result = statement.executeQuery(query);
            metaData = result.getMetaData();
            int numberOfColumns = metaData.getColumnCount();

            while (result.next()) {
                Object[] rowData = new Object[numberOfColumns];
                for (int i = 1; i <= numberOfColumns; i++) {
                    rowData[i - 1] = result.getObject(i);
                }
                model.addRow(rowData);
            }

            result.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(component, "", "", JOptionPane.ERROR_MESSAGE);
        }
        return model;
    }
    
    public void addCategoryValue(String[] values){
        String query = "INSERT INTO "+categoryTable+" ("+categoryColumns[1]+","+categoryColumns[2]+") VALUES (?,?)";
       
        try{
            prepare = connection.prepareStatement(query);
            prepare.setObject(1, values[0]);
            prepare.setObject(2, values[1]);
            
            prepare.executeUpdate();
            prepare.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(component, "", "", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void EditCategoryValue(Object newVal, int colIdx, Object ID){
        String query = "UPDATE "+categoryTable+" SET "+categoryColumns[colIdx]+" = ? WHERE ("+categoryColumns[0]+" = ?)";
        
        try{
            prepare = connection.prepareStatement(query);
            prepare.setObject(1, newVal);
            prepare.setObject(2, ID);
            
            
            prepare.executeUpdate();
            prepare.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(component, "", "", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void DeleteCategoryRecord(Object ID){
        String query = "DELETE FROM "+categoryTable+" WHERE "+categoryColumns[0]+" = ?";
        
        try{
            prepare = connection.prepareStatement(query);
            prepare.setObject(1, ID);
            
            prepare.executeUpdate();
            prepare.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(component, "", "", JOptionPane.ERROR_MESSAGE);
        }
    }
}
