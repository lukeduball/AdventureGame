package adventure;
import java.util.ArrayList;

public class StoryNode{
   private String storyText;
   private String leftChoice;
   private String rightChoice;
   private ArrayList<String> items;
   
   public StoryNode(String text, String c1, String c2, String stuff){
      storyText = text;
      leftChoice = c1;
      rightChoice = c2;
      items = new ArrayList<>();  // TODO: implementation of items
   }
   
   public String getStory(){
      return storyText;
   }

   public String getLeftChoice(){
      return leftChoice;
   }
   
   // TODO: implementation
   public String getRightChoice(){
      return rightChoice;
   }
   
   // TODO: implementation   
   public String getItem( int i ){
      return "";
   }
   
   public void putItem( String s ){
   }
   
   @Override
   public String toString(){
      return storyText.substring(0,10);
   }
     
}