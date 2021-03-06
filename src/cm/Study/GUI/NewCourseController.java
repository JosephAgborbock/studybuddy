
/*
    This is the only controller for this project. This is due to some complcations with the Netbeans IDE. This is not
    a ggod programming methodology. So please do not use one controller in your projects.

*/
package cm.Study.GUI;

import cm.Study.DAO.CourseDao;
import cm.Study.DAO.Course_DayDAO;
import cm.Study.DAO.DayDAO;
import cm.Study.Entities.Course;
import cm.Study.Entities.Course_Day;
import cm.Study.Entities.Days;
import cm.Study.Main.Main;
import com.jfoenix.controls.JFXTextField;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Cressence
 */
public class NewCourseController {
    NewCourse stageCourse;
    NewDay stDay;
    TimeTable staTimeTable;
    public Days days;

    public NewCourseController(NewCourse stageCourse, NewDay stDay, TimeTable staTimeTable) throws SQLException {
        this.stageCourse = stageCourse;
        this.stDay = stDay;
        this.staTimeTable = staTimeTable;
        initialize();
    }

   
    
    public void initialize() throws SQLException{
        
        /*
            This section of this controller class deals with the new coures controller.
        */
        stageCourse.courseScene = new Scene(stageCourse.root, 679, 477);
        stageCourse.courseScene.getStylesheets().add(Main.class.getResource("styles.css").toExternalForm());
        
        //title
        stageCourse.imageView = getImageView(stageCourse.studyImage, 70, 70);
        stageCourse.titleLb.setPadding(new Insets(0,0,0,20));
        stageCourse.titleHBox.setSpacing(9);
        stageCourse.titleHBox.setPadding(new Insets(30,0,0,0));
        stageCourse.titleHBox.setAlignment(Pos.TOP_CENTER);
        stageCourse.titleHBox.getChildren().addAll(stageCourse.imageView,stageCourse.titleLb );
        stageCourse.titleLb.getStyleClass().add("title-label");
        
        //course
        stageCourse.validator.setMessage("Value required");
        stageCourse.courseTf.getValidators().add(stageCourse.validator);
        stageCourse.courseVb.setSpacing(5);
        stageCourse.courseVb.getChildren().addAll(stageCourse.courseLb, stageCourse.courseTf);
        
        //credit
        stageCourse.creditValueTf.getValidators().add(stageCourse.validator);
        stageCourse.creditVb.setSpacing(5);
        stageCourse.creditVb.getChildren().addAll(stageCourse.creditValueLb, stageCourse.creditValueTf);
        numericTF(stageCourse.creditValueTf);
        
        //radiobutton
        stageCourse.difficulRadioButton.setToggleGroup(stageCourse.opinionGroup);
        stageCourse.difficulRadioButton.setPadding(new Insets(10));
        stageCourse.difficulRadioButton.setUserData(this.stageCourse.calculationString);
        stageCourse.likeRadioButton.setToggleGroup(stageCourse.opinionGroup);
        stageCourse.likeRadioButton.setPadding(new Insets(10));
        stageCourse.likeRadioButton.setUserData(this.stageCourse.likeString);
        stageCourse.notInterRadioButton.setToggleGroup(stageCourse.opinionGroup);
        stageCourse.notInterRadioButton.setPadding(new Insets(10));
        stageCourse.notInterRadioButton.setUserData(this.stageCourse.notinterString);
        stageCourse.radioVBox.getChildren().addAll(stageCourse.radioLabel,stageCourse.difficulRadioButton, stageCourse.notInterRadioButton,
                stageCourse.likeRadioButton);
        
        //right
        stageCourse.rightVBox.getChildren().addAll(stageCourse.courseListView, stageCourse.nextButton);
        stageCourse.rightVBox.setSpacing(20);
        stageCourse.nextButton.setOnAction(e -> stageCourse.getScene().setRoot(stDay.root));
        stageCourse.nextButton.getStyleClass().add("button-normal");
        stageCourse.rightVBox.setPadding(new Insets(0,0,10,0));
        stageCourse.courseListView.setPrefHeight(285);
        stageCourse.courseListView.setPrefWidth(220);
        
        
        
       //populate listview
        showCourse();
        
        //delete course
        stageCourse.deleteItem.setOnAction(e -> {
            stageCourse.courseListView.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends Label> ov, Label oldValueString, Label newValue)-> {
            try {
                deleteCourse(newValue.getText());
            } catch (SQLException ex) {
                Logger.getLogger(NewCourseController.class.getName()).log(Level.SEVERE, null, ex);
            }
                }
        );
        });
        
         
        //context menu
        stageCourse.listContextMenu.getItems().addAll(stageCourse.deleteItem, stageCourse.updateItem);
        stageCourse.courseListView.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event)->{
            if (event.getButton() == MouseButton.SECONDARY) {
                for (int i = 0; i < stageCourse.courseListView.getItems().size(); i++) {
                      stageCourse.listContextMenu.show((Node) stageCourse.courseListView.getItems().get(i), event.getScreenX(), event.getScreenY());
                }
              
            }
        });
        
        //left
        stageCourse.leftVBox.getChildren().addAll(stageCourse.courseVb, stageCourse.creditVb,
                stageCourse.radioVBox, stageCourse.okButton);
        stageCourse.leftVBox.setSpacing(12);
        stageCourse.okButton.getStyleClass().add("button-normal");
               
            //save course
             stageCourse.okButton.setOnAction(e ->
        {
            Integer credit = Integer.parseInt(stageCourse.creditValueTf.getText());
            String myopinionString = stageCourse.opinionGroup.getSelectedToggle().getUserData().toString();
            try {
                saveCourse(stageCourse.courseTf.getText(), credit, myopinionString);
            } catch (SQLException ex) {
                Logger.getLogger(NewCourseController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            stageCourse.courseListView.getItems().clear();
            try {
                showCourse();
            } catch (SQLException ex) {
                Logger.getLogger(NewCourseController.class.getName()).log(Level.SEVERE, null, ex);
            }
     });         
        
        //middle
        stageCourse.middleHBox.getChildren().addAll(stageCourse.leftVBox, stageCourse.rightVBox);
        stageCourse.middleHBox.setAlignment(Pos.CENTER);
        stageCourse.middleHBox.setPadding(new Insets(20,0,0,0));
        stageCourse.middleHBox.setSpacing(60);
        
        //root
        stageCourse.root.getChildren().addAll(stageCourse.titleHBox, stageCourse.middleHBox); 
        stageCourse.root.getStyleClass().add("main-background");
        
        
        
        //*****************************************************************************************
       
        //register free day
        //tittle
        stDay.imageView = getImageView(stDay.studyImage, 70, 70);
        stDay.titleLb.setPadding(new Insets(0,0,0,20));
        stDay.titleHBox.setSpacing(9);
        stDay.titleHBox.setPadding(new Insets(30,0,0,0));
        stDay.titleHBox.setAlignment(Pos.TOP_CENTER);
        stDay.titleHBox.getChildren().addAll(stDay.imageView,stDay.titleLb );
        stDay.titleLb.getStyleClass().add("title-label");
        stDay.nbLabel.setPadding(new Insets(0, 0, 0, 110));
        
        //left
        stDay.dayCombo.getItems().addAll("Monday",
                "Teusday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday",
                "Sunday");
        stDay.daysVBox.getChildren().addAll(stDay.daysLb, stDay.dayCombo);
        
        stDay.timeFrom.setShowTime(true);
        stDay.froVBox.getChildren().addAll(stDay.fromLabel, stDay.timeFrom);
        
        stDay.timeTo.setShowTime(true);
        stDay.toVBox.getChildren().addAll(stDay.toLabel, stDay.timeTo);
        
        stDay.leftVBox.getChildren().addAll(stDay.daysVBox, stDay.froVBox, stDay.toVBox, stDay.okButton);
        stDay.leftVBox.setSpacing(30);
        stDay.leftVBox.setPadding(new Insets(0, 0, 0, 15));
        stDay.okButton.getStyleClass().add("button-normal");
        
        //save day
        Label timeLabel = new Label();
        Label timeToLabel = new Label();        
        
        stDay.okButton.setOnAction(e ->{
            
            try {
                System.out.println(stDay.timeFrom.getValue());
                System.out.println(stDay.timeTo.getValue());
                saveDay(stDay.dayCombo.getValue().toString(), stDay.timeFrom.getTime().toString(), stDay.timeTo.getTime().toString());
            } catch (SQLException ex) {
                Logger.getLogger(NewCourseController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            stDay.dayListView.getItems().clear();
            try {
                showDay();
            } catch (SQLException ex) {
                Logger.getLogger(NewCourseController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        //right
        stDay.buttonHBox.getChildren().addAll(stDay.previousButton, stDay.nextButton);
        stDay.buttonHBox.setSpacing(110);
        stDay.previousButton.setOnAction(e -> 
        {
            stageCourse.getScene().setRoot(stageCourse.root);
        });
        stDay.nextButton.setOnAction(e ->{
            try {
                clearDb();
                matchCourseDay();
            } catch (SQLException ex) {
                Logger.getLogger(NewCourseController.class.getName()).log(Level.SEVERE, null, ex);
            }
            stageCourse.getScene().setRoot(staTimeTable.tableroot);
            try {
                showTable();
            } catch (SQLException ex) {
                Logger.getLogger(NewCourseController.class.getName()).log(Level.SEVERE, null, ex);
            }
                });
        stDay.previousButton.getStyleClass().add("button-normal");
        stDay.nextButton.getStyleClass().add("button-normal");
        stDay.rightVBox.getChildren().addAll(stDay.dayListView, stDay.buttonHBox);
        stDay.rightVBox.setSpacing(12);
        stDay.rightVBox.setPadding(new Insets(0,0,0,12));
        stDay.buttonHBox.setPadding(new Insets(0,0,0,7));
        stDay.dayListView.setPrefHeight(245);
        
        //show days
        showDay();
        
        //middle
        stDay.middleHBox.getChildren().addAll(stDay.leftVBox, stDay.rightVBox);
        stDay.middleHBox.setAlignment(Pos.CENTER);
        stDay.middleHBox.setPadding(new Insets(20,0,0,0));
        stDay.middleHBox.setSpacing(15);
        
        //root
        stDay.root.getChildren().addAll(stDay.titleHBox, stDay.nbLabel, stDay.middleHBox);
        stDay.root.setSpacing(10);
        stDay.root.getStyleClass().add("main-background");
        
        //****************************************************************************************************
        //timetable
        
        Label mondayLabel = new Label("MONDAY");
         mondayLabel.getStyleClass().add("title-labels");
         Label teusdayLabel = new Label("TEUSDAY");
         teusdayLabel.getStyleClass().add("title-labels");
         Label wednesdayLabel = new Label("WEDNESDAY");
         wednesdayLabel.getStyleClass().add("title-labels");
         Label thursdayLabel = new Label("THURSDAY");
         thursdayLabel.getStyleClass().add("title-labels");
         Label fridayLabel = new Label("FRIDAY");
         fridayLabel.getStyleClass().add("title-labels");
         Label saturdayLabel = new Label("SATURDAY");
         saturdayLabel.getStyleClass().add("title-labels");
         Label sundayLabel = new Label("SUNDAY");
         sundayLabel.getStyleClass().add("title-labels");
         
         
         staTimeTable.mondayBox.getChildren().add(mondayLabel);
        staTimeTable.teusdayBox.getChildren().add(teusdayLabel);
        staTimeTable.wednesdayBox.getChildren().add(wednesdayLabel);
        staTimeTable.thursdayBox.getChildren().add(thursdayLabel);
        staTimeTable.fridayBox.getChildren().add(fridayLabel);
        staTimeTable.saturdayBox.getChildren().add(saturdayLabel);
        staTimeTable.sundayBox.getChildren().add(sundayLabel);
        
        //tittle
          staTimeTable.title.getStyleClass().add("title-label");
        
        staTimeTable.menubar.getMenus().addAll(staTimeTable.fileMenu, staTimeTable.editMenu, staTimeTable.aboutMenu,
                staTimeTable.helpMenu);
        staTimeTable.fileMenu.getItems().addAll(staTimeTable.save, staTimeTable.print, staTimeTable.exitM);
        staTimeTable.exitM.setOnAction((ActionEvent t)->
        {System.exit(0);});
        staTimeTable.print.setAccelerator(KeyCombination.keyCombination("Ctrl+P"));
        staTimeTable.save.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));
        staTimeTable.print.setOnAction(e ->print(staTimeTable.tableVb));
        staTimeTable.editMenu.setOnAction(e -> {
            //what to edit
        });
       staTimeTable.save.setOnAction(e -> {
           stageCourse.close();
            try {
                newTimetable();
                newCourse();
            } catch (SQLException ex) {
                Logger.getLogger(NewCourseController.class.getName()).log(Level.SEVERE, null, ex);
            }
       });
               
          
          //middle
       staTimeTable.mondayBox.getStyleClass().add("time-table");
       staTimeTable.teusdayBox.getStyleClass().add("time-table");
       staTimeTable.wednesdayBox.getStyleClass().add("time-table");
       staTimeTable.thursdayBox.getStyleClass().add("time-table");
       staTimeTable.fridayBox.getStyleClass().add("time-table");
       staTimeTable.saturdayBox.getStyleClass().add("time-table");
       staTimeTable.sundayBox.getStyleClass().add("time-table");
       staTimeTable.mondayBox.setMinWidth(180);
       staTimeTable.teusdayBox.setMinWidth(180);
       staTimeTable.wednesdayBox.setMinWidth(180);
       staTimeTable.thursdayBox.setMinWidth(180);
       staTimeTable.fridayBox.setMinWidth(180);
       staTimeTable.saturdayBox.setMinWidth(180);
       staTimeTable.sundayBox.setMinWidth(180);
       staTimeTable.firstHBox.setSpacing(40);
       staTimeTable.secondHBox.setSpacing(40);
       staTimeTable.thirdHBox.setSpacing(40);
       
       staTimeTable.firstHBox.getChildren().addAll(staTimeTable.mondayBox, staTimeTable.teusdayBox,
                  staTimeTable.wednesdayBox);
       staTimeTable.secondHBox.getChildren().addAll(staTimeTable.thursdayBox, staTimeTable.fridayBox,
                  staTimeTable.saturdayBox );
       staTimeTable.thirdHBox.getChildren().add(staTimeTable.sundayBox);
          staTimeTable.tableVb.getChildren().addAll(staTimeTable.title, staTimeTable.firstHBox,
                  staTimeTable.secondHBox, staTimeTable.thirdHBox);
          staTimeTable.tableVb.setSpacing(13);
          staTimeTable.tableVb.setPadding(new Insets(8, 0, 13, 0));
          
          //the back button
          staTimeTable.backBtn.setOnAction(e -> stageCourse.getScene().setRoot(stageCourse.root));
          
          //root
          staTimeTable.tableroot.getStyleClass().add("main-background");
          staTimeTable.tableroot.setTop(staTimeTable.menubar);
          staTimeTable.tableroot.setCenter(staTimeTable.tableVb);
          staTimeTable.tableroot.setBottom(staTimeTable.backBtn);
          staTimeTable.backBtn.getStyleClass().add("button-normal");
          staTimeTable.tableroot.setPadding(new Insets(0,10,8,10));
          
        
        //layout
        stageCourse.getIcons().add(stageCourse.image);
        stageCourse.setScene(stageCourse.courseScene);
        stageCourse.setResizable(false);
        stageCourse.showAndWait();
    }
    
    
    public ImageView getImageView(Image image, double fitWidth, double fitHeight){
        ImageView view = new ImageView(image);
        view.setFitHeight(fitHeight);
        view.setFitWidth(fitWidth);
        
        return view;
    }
    
    private void numericTF(JFXTextField textField){
        textField.focusedProperty().addListener((o,oldVal,newVal)->{                    
    if(!newVal) textField.validate();                    
    });
        textField.textProperty().addListener(new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        }
    });
    }
    
    public void print (Node node){
               PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(node.getScene().getWindow())){
        boolean success = job.printPage(node);
        if (success) {
            job.endJob();
        }
      }
    }
    
public void saveCourse(String courseName, int credit_value, String type) throws SQLException{
        Course course = new Course(courseName, credit_value, type);
        CourseDao courseDao = new CourseDao();
        course.setCourse_Id(courseDao.getLastId()+1);
        courseDao.saveCourse(course);        
        stageCourse.courseTf.clear();
        stageCourse.creditValueTf.clear();
        
    }

public void showCourse() throws SQLException{ 
        Course course;
        Label itemLabel;
        CourseDao courseDao = new CourseDao();
        for(int i=0; i<courseDao.showCourses().size(); i++){  
            course = new Course();
            course =  courseDao.showCourses().remove(i);
            itemLabel = new Label();
            itemLabel.setText(course.getCourse_Name());
          stageCourse.courseListView.getItems().add(itemLabel);
        }
       
    }

 public void updateCourse(Course course) throws SQLException{
        CourseDao courseDao = new CourseDao();
        courseDao.updateCourse(course);
    }
    
    public void deleteCourse(String courseName) throws SQLException{
        CourseDao courseDao = new CourseDao();
        courseDao.deleteCourse(courseName);
        
    }
    public void clickShowCourse(){
        
    }
    
    //******************************************************************************************************
    
    public void saveDay(String theDay, String from, String dayto) throws SQLException{
        Days day = new Days(theDay, from, dayto);
        DayDAO dayDao = new DayDAO();
        day.setDay_Id(dayDao.getLastId() + 1);
        dayDao.saveDay(day);
    }
    
    public void showDay() throws SQLException{
        Days days;
        DayDAO dayDAO = new DayDAO();
        Label itemLabel;
        
        for (int i = 0; i < dayDAO.showDays().size(); i++) {
            days = new Days();
            days = dayDAO.showDays().remove(i);
            itemLabel = new Label();
            itemLabel.setText(days.getDay_Name().concat(" ").concat(days.getFree_From()).concat("  -  ").
                    concat(days.getFree_To()));
            stDay.dayListView.getItems().add(itemLabel);
        }
    }
    
    //*****************************************************************************************************
    public void saveTableData(int Course_Id, int Day_Id) throws SQLException{
        
        Course_Day course_Day = new Course_Day( Course_Id, Day_Id);
        Course_DayDAO course_DayDAO = new Course_DayDAO();
        course_Day.setCourseDay_id(course_DayDAO.getLastId() + 1);
        course_DayDAO.saveCourse_Day(course_Day);
    }
    
    public  void showTable() throws SQLException{
        Course course;
        CourseDao courseDao = new CourseDao();
        Course_DayDAO course_DayDAO = new Course_DayDAO();
        Course_Day course_Day;
        Days days;
        String courseString, dayFromString, dayToString;
        DayDAO dayDAO = new DayDAO();
         Label text;
         int count = 0;
                 
         ArrayList<Label> mondaArrayList = new ArrayList();
         ArrayList<Label> teusArrayList = new ArrayList();
         ArrayList<Label> wednesdayArrayList = new ArrayList();
         ArrayList<Label> thursdayArrayList = new ArrayList();
         ArrayList<Label> fridayArrayList = new ArrayList();
         ArrayList<Label> saturdayArrayList = new ArrayList();
         ArrayList<Label> sundayArrayList = new ArrayList();
        for (int i = 0; i < course_DayDAO.showCourse_Days().size(); i++) {
            course_Day = new Course_Day();
            course_Day = course_DayDAO.showCourse_Days().remove(i);
            
            //compare day_course to day class
            for (int j = 0; j < dayDAO.showDays().size(); j++) {
            days = new Days();
            days = dayDAO.showDays().remove(j);
           
                if (course_Day.getDay_Id() == days.getDay_Id()) {
                    dayFromString = days.getFree_From();
                    dayToString = days.getFree_To();
                                        
                   
                    //compare day_course with course class
                        for(int k=0; k <courseDao.showCourses().size(); k++){  
                            course = new Course();
                            course =  courseDao.showCourses().remove(k);
                           
                                if (course_Day.getCourse_Id() == course.getCourse_Id()) {
                                    courseString = course.getCourse_Name();
                                    System.out.print(days.getDay_Name() + "  " + dayFromString+ " " +
                            dayToString + " ");
                                System.out.println(courseString);
                                count++;
                                text = new Label();
                                text.setText(courseString + "\n " +dayFromString+ " - " +dayToString);
                                
                                    if ("Monday".equals(days.getDay_Name())) {
                                        mondaArrayList.add(text);
                                    }else if("Teusday".equals(days.getDay_Name())){
                                        teusArrayList.add(text);
                                }else if("Wednesday".equals(days.getDay_Name())){
                                    wednesdayArrayList.add(text);
                                }else if("Thursday".equals(days.getDay_Name())){
                                    thursdayArrayList.add(text);
                                }else if("Friday".equals(days.getDay_Name())){
                                    fridayArrayList.add(text);
                                }else if("Saturday".equals(days.getDay_Name())){
                                    saturdayArrayList.add(text);
                                }else if("Sunday".equals(days.getDay_Name())){
                                    sundayArrayList.add(text);
                                }
                                    
                                //Solve this problem to display the content.
                                System.out.println();
                            }
                                 
                        }
                }
            }
        }
        
        for (int i = 0; i < mondaArrayList.size(); i++) {
            staTimeTable.mondayBox.getChildren().add(mondaArrayList.get(i));
        }
        for (int i = 0; i < teusArrayList.size(); i++) {
            staTimeTable.teusdayBox.getChildren().add(teusArrayList.get(i));
        }
        for (int i = 0; i < wednesdayArrayList.size(); i++) {
            staTimeTable.wednesdayBox.getChildren().add(wednesdayArrayList.get(i));
        }
        for (int i = 0; i < thursdayArrayList.size(); i++) {
            staTimeTable.thursdayBox.getChildren().add(thursdayArrayList.get(i));
        }
        for (int i = 0; i < fridayArrayList.size(); i++) {
            staTimeTable.fridayBox.getChildren().add(fridayArrayList.get(i));
        }
        for (int i = 0; i < saturdayArrayList.size(); i++) {
            staTimeTable.saturdayBox.getChildren().add(saturdayArrayList.get(i));
        }
        for (int i = 0; i < sundayArrayList.size(); i++) {
            staTimeTable.sundayBox.getChildren().add(sundayArrayList.get(i));
        }
        
    }
    
    //this function saves the course and day according to the level of difficulty
    public void matchCourseDay() throws SQLException{
        DayDAO dayDao = new DayDAO();        
        CourseDao courseDao = new CourseDao();
        ArrayList randArrayList = new ArrayList();
        int num, courseId;
        
        if (dayDao.showDays().size() == courseDao.showCourses().size()) {
            for (int i = 0; i < dayDao.showDays().size(); i++) {
                days = new Days();
                days = dayDao.showDays().remove(i);
                num = courseDao.randomId();
                if (!randArrayList.contains(num)) {
                    randArrayList.add(num);
                    saveTableData(num, days.getDay_Id());
                } else {
                    check(randArrayList);
                }
            }
        }
    }
    
    public void clearDb() throws SQLException{
        Course_DayDAO cddao = new Course_DayDAO();
        cddao.deleteCourseDay();
    }
    
    public void  newTimetable() throws SQLException{
        DayDAO dayDAO = new DayDAO();
        CourseDao courseDao = new CourseDao();
        dayDAO.deleteAll();
        courseDao.deleteAll();
    }
   
    
    public ArrayList getCourse() throws SQLException{ 
        Course course;
        ArrayList couArrayList = null;
        CourseDao courseDao = new CourseDao();
        for(int i=0; i<courseDao.showCourses().size(); i++){  
            course = new Course();
            course =  courseDao.showCourses().remove(i);
            couArrayList.add(course);
        }
       return couArrayList;
    }
    
    public  void check(ArrayList randArrayList) throws SQLException{
        int num;
        CourseDao courseDao = new CourseDao();
        num = courseDao.randomId();
        if (!randArrayList.contains(num)) {
                    randArrayList.add(num);
                   saveTableData(num, days.getDay_Id());
        } else {
            check(randArrayList);
        }
    }
    
    public void newCourse() throws SQLException{
        NewCourse newCourse = new NewCourse();
        NewDay newDay = new NewDay();
        TimeTable newTimeTable = new TimeTable();
        NewCourseController newCourseController = new NewCourseController(newCourse, newDay, newTimeTable);
    }
}
