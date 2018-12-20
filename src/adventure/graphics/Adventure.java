package adventure.graphics;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import adventure.StoryNode;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.datastructures.LinkedBinaryTree;
import net.datastructures.LinkedStack;
import net.datastructures.Position;

public class Adventure extends Application
{
	private static Text storyText;
	private static Position<StoryNode> currentPosition;
	private static GridPane choicesPane;
	private static Button backBtn;
	private static GridPane replayEndPane;
	private static VBox vContainer;
	
	@Override
	public void start(Stage stage)
	{
		try
		{
			//Opens the file chooser to select a file to read the story from
			FileChooser fileChooser = new FileChooser();
			fileChooser.setInitialDirectory(new File("."));
			File file = fileChooser.showOpenDialog(stage);
			if(file == null)
				System.exit(0);
			
			LinkedBinaryTree<StoryNode> storyTree = loadStory(file);
			
			BorderPane border = new BorderPane();
			initStoryBtnEvents(storyTree);
			border.setCenter(createBasicWindow(border));
			updateStory(storyTree, storyTree.root());
			Scene scene = new Scene(border, 400, 400);
			scene.getStylesheets().add("adventure/graphics/stylesheet_adventure.css");
			stage.setScene(scene);
			stage.setTitle("Adventure");
			stage.show();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Initializes the back, replay, and exit button and sets their pressed events
	 * @param storyTree container of the story
	 */
	private static void initStoryBtnEvents(LinkedBinaryTree<StoryNode> storyTree)
	{
		backBtn = new Button("Back");
		backBtn.setOnAction(e->{
			updateStory(storyTree, storyTree.parent(currentPosition));
		});
		
		replayEndPane = new GridPane();
		replayEndPane.setAlignment(Pos.CENTER);
		replayEndPane.setHgap(10.0);
		Button exitBtn = new Button("Exit");
		
		exitBtn.setOnAction(e->{
			Platform.exit();
			System.exit(0);
		});
		
		Button replayBtn = new Button("Replay");
		
		replayBtn.setOnAction(e->{
			updateStory(storyTree, storyTree.root());
			vContainer.getChildren().remove(replayEndPane);
			vContainer.getChildren().add(backBtn);
		});
		
		replayEndPane.add(exitBtn, 1, 0);
		replayEndPane.add(replayBtn, 0, 0);
	}
	
	public static void main(String[] args)
	{
		launch(args);
	}
	
	/**
	 * Creates a VBox which contains the basic layout of the graphical interface of the story
	 * @param parent 
	 * @return returns the layout of the basic window
	 */
	public VBox createBasicWindow(BorderPane parent)
	{
		choicesPane = new GridPane();
		choicesPane.setHgap(10.0);
		choicesPane.setPadding(new Insets(10.0, 0.0, 10.0, 0.0));
		choicesPane.setAlignment(Pos.CENTER);
		
		storyText = new Text();
		storyText.setTextAlignment(TextAlignment.CENTER);
		storyText.wrappingWidthProperty().bind(parent.widthProperty());
		
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(storyText);
		scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		
		vContainer = new VBox();
		vContainer.setAlignment(Pos.CENTER);
		
		vContainer.getChildren().addAll(scrollPane, choicesPane, backBtn);
		return vContainer;
	}
	
	/**
	 * Updates the story to a new position on the tree and refreshes data within the buttons
	 * @param storyTree
	 * @param node the node position to switch to
	 */
	public static void updateStory(LinkedBinaryTree<StoryNode> storyTree, Position<StoryNode> node)
	{
		//Note:: the choices pane and choices button are re-created each update of the story for future implementations of multiple choices in a non-binary tree
		choicesPane.getChildren().clear();
		currentPosition = node;
		storyText.setText(node.getElement().getStory());
		
		if(storyTree.isRoot(node))
		{
			backBtn.setDisable(true);
		}
		else
		{
			backBtn.setDisable(false);
		}
		
		if(!storyTree.isExternal(node))
		{
			if(!node.getElement().getLeftChoice().equals("null"))
			{
				Button choiceButton = new Button(node.getElement().getLeftChoice());
				
				choiceButton.setOnAction(e->{
					updateStory(storyTree, storyTree.left(currentPosition));
				});
				
				choicesPane.add(choiceButton, 0, 0);
			}
			
			if(!node.getElement().getRightChoice().equals("null"))
			{
				Button choiceButton = new Button(node.getElement().getRightChoice());
				
				choiceButton.setOnAction(e->{
					updateStory(storyTree, storyTree.right(currentPosition));
				});
				
				choicesPane.add(choiceButton, 1, 0);
			}	
		}
		else
		{
			vContainer.getChildren().remove(backBtn);
			vContainer.getChildren().add(replayEndPane);
		}
	}
	
	private static LinkedBinaryTree<StoryNode> loadStory(File file) throws Exception
	{
		Scanner fileInput = new Scanner(file);
		
		LinkedStack<LinkedBinaryTree<StoryNode>> stack = new LinkedStack<>();
		
		while(fileInput.hasNextLine())
		{
			String currentLine = fileInput.nextLine();
			String storyText = null;
			String leftChoice = null;
			String rightChoice = null;
			ArrayList<String> items = new ArrayList<>();
			if(currentLine.equals("Empty"))
			{
				storyText = "Empty";
				leftChoice = "null";
				rightChoice = "null";
				currentLine = "end";
			}
			else
			{
				storyText = currentLine;
				if(fileInput.hasNextLine())
					currentLine = fileInput.nextLine();
				else
				{
					fileInput.close();
					throw new Exception("Error loading this file!");
				}
				while(!currentLine.equals("end") && !currentLine.equals("mid"))
				{
					if(fileInput.hasNextLine())
					{
						if(leftChoice == null)
						{
							leftChoice = currentLine;
						}
						else if(rightChoice == null)
						{
							rightChoice = currentLine;
						}
						else if(!currentLine.equals("none"))
						{
							items.add(currentLine);
						}
						currentLine = fileInput.nextLine();
					}
					else
					{
						fileInput.close();
						throw new Exception("Error loading this file!");
					}
				}
			}
			LinkedBinaryTree<StoryNode> tree = new LinkedBinaryTree<>();
			StoryNode storyNode = new StoryNode(storyText, leftChoice, rightChoice, "");
			tree.addRoot(storyNode);
			if(currentLine.equals("end"))
			{
				stack.push(tree);
			}
			else
			{
				if(stack.size() < 2)
				{
					fileInput.close();
					throw new Exception("Error loading this file!");
				}
				else
				{
					LinkedBinaryTree<StoryNode> rightSubTree = stack.pop();
					if(rightSubTree.root().getElement().getStory().equals("Empty"))
					{
						rightSubTree = new LinkedBinaryTree<>();
					}
					LinkedBinaryTree<StoryNode> leftSubTree = stack.pop();
					if(leftSubTree.root().getElement().getStory().equals("Empty"))
					{
						leftSubTree = new LinkedBinaryTree<>();
					}
					tree.attach(tree.root(), leftSubTree, rightSubTree);
					stack.push(tree);
				}
			}
		}
		
		fileInput.close();
		return stack.pop();
	}

}
