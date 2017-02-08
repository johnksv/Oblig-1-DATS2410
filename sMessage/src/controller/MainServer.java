package controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Base64;

/**
 *
 * @author s305046, s305080, s305084, s305089
 */
public class MainServer extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
	Parent root = FXMLLoader.load(getClass().getResource("/view/Server.fxml"));
	
	Scene scene = new Scene(root);
	
	stage.setScene(scene);
	stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
	    launch(args);
    }
    
}
