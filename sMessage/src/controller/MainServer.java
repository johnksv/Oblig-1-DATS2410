package controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * Starts the server.
 * @author Member(1-2-3-4)
 */
public class MainServer extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
	Parent root = FXMLLoader.load(getClass().getResource("/view/Server.fxml"));
	stage.setTitle("sMessage - Server");
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
