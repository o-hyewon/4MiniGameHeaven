package TetrisGame;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import CommonService.CommonService;
import CommonService.CommonServiceImpl;
import DataBase.DataBaseService;
import DataBase.DataBaseServiceImpl;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Tetris extends Application {
   // The variables
   public static final int MOVE = 25;
   public static final int SIZE = 25;
   public static int XMAX = SIZE * 12;
   public static int YMAX = SIZE * 24;
   public static int[][] MESH = new int[XMAX / SIZE][YMAX / SIZE];
   private static Pane group = new Pane();
   private static Form object;
   private static Scene scene = new Scene(group, XMAX + 150, YMAX);
   public static int score = 0;
   private static int top = 0;
   private static boolean game = true;
   private static Form nextObj = Controller.makeRect();
   private static int linesNo = 0;
   TimerTask task;
   Timer fall;
   Text scoretext;
   Text level;
   boolean pauseFlag = false;
   CommonService comSrv = new CommonServiceImpl();
   DataBaseService dbSrv = new DataBaseServiceImpl();

   @Override
   public void start(Stage stage) throws Exception {
      for (int[] a : MESH) {
         Arrays.fill(a, 0);
      }
      Line line = new Line(XMAX, 0, XMAX, YMAX);
      
      group.getChildren().clear(); //화면 초기화
      
      scoretext = new Text("Score: ");
      scoretext.setStyle("-fx-font: 20 arial;");
      scoretext.setY(50);
      scoretext.setX(XMAX + 5);

      level = new Text("Lines: ");
      level.setStyle("-fx-font: 20 arial;");
      level.setY(100);
      level.setX(XMAX + 5);
      level.setFill(Color.GREEN);
      
      // pause 버튼
      Button pauseBtn = new Button("Pause");
      pauseBtn.setOnAction(e -> {
         togglePause();
      });
      pauseBtn.setStyle("-fx-font: 20 arial;");
      pauseBtn.setLayoutY(130);
      pauseBtn.setLayoutX(XMAX + 5);
      
      Label label1 = new Label();
      label1.setText("***조작법*** ");
      label1.setStyle("-fx-font: 18 arial;");
      label1.setLayoutY(200);
      label1.setLayoutX(XMAX+5);
      
      Label label2 = new Label();
      label2.setText("방향키 ↑ : 블럭 바꾸기 ");
      label2.setLayoutY(230);
      label2.setLayoutX(XMAX+5);
      
      Label label3 = new Label();
      label3.setText("방향키 ↓ : 아래로 이동");
      label3.setLayoutY(260);
      label3.setLayoutX(XMAX+5);
      
      Label label4 = new Label();
      label4.setText("방향키 ← : 왼쪽 이동");
      label4.setLayoutY(290);
      label4.setLayoutX(XMAX+5);
      
      Label label5 = new Label();
      label5.setText("방향키 → : 오른쪽 이동");
      label5.setLayoutY(320);
      label5.setLayoutX(XMAX+5);
      
      Label label6 = new Label();
      label6.setText("P 키 : 게임 중지");
      label6.setLayoutY(350);
      label6.setLayoutX(XMAX+5);
      
      group.getChildren().addAll(scoretext, line, level, pauseBtn,label1,
    		  label2,label3,label4,label5,label6);

      Form a = nextObj;
      group.getChildren().addAll(a.a, a.b, a.c, a.d);
      moveOnKeyPress(a);
      object = a;
      nextObj = Controller.makeRect();
      stage.setScene(scene);
      stage.setTitle("T E T R I S");
      stage.show();

      fall = new Timer();
      task = makeTask();
      fall.schedule(task, 0, 100);
   }

   void togglePause() {
      if (pauseFlag == false) {
         pauseFlag = true;
         pause();
      } else {
         pauseFlag = false;
         resume();
      }
   }

   private void moveOnKeyPress(Form form) {//
      scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
         @Override
         public void handle(KeyEvent event) {
            switch (event.getCode()) {
            case RIGHT:
               if (!pauseFlag)
                  Controller.MoveRight(form);
               break;
            case DOWN:
               if (!pauseFlag) {
                  MoveDown(form);
                  score++;
               }
               break;
            case LEFT:
               if(!pauseFlag)
                  Controller.MoveLeft(form);
               break;
            case UP:
               if(!pauseFlag)
                  MoveTurn(form);
               break;
            case P:
               togglePause();
               break;
            }
         }
      });
   }

   private void MoveTurn(Form form) {
      int f = form.form;
      Rectangle a = form.a;
      Rectangle b = form.b;
      Rectangle c = form.c;
      Rectangle d = form.d;
      switch (form.getName()) {
      case "j":
         if (f == 1 && cB(a, 1, -1) && cB(c, -1, -1) && cB(d, -2, -2)) {
            MoveRight(form.a);
            MoveDown(form.a);
            MoveDown(form.c);
            MoveLeft(form.c);
            MoveDown(form.d);
            MoveDown(form.d);
            MoveLeft(form.d);
            MoveLeft(form.d);
            form.changeForm();
            break;
         }
         if (f == 2 && cB(a, -1, -1) && cB(c, -1, 1) && cB(d, -2, 2)) {
            MoveDown(form.a);
            MoveLeft(form.a);
            MoveLeft(form.c);
            MoveUp(form.c);
            MoveLeft(form.d);
            MoveLeft(form.d);
            MoveUp(form.d);
            MoveUp(form.d);
            form.changeForm();
            break;
         }
         if (f == 3 && cB(a, -1, 1) && cB(c, 1, 1) && cB(d, 2, 2)) {
            MoveLeft(form.a);
            MoveUp(form.a);
            MoveUp(form.c);
            MoveRight(form.c);
            MoveUp(form.d);
            MoveUp(form.d);
            MoveRight(form.d);
            MoveRight(form.d);
            form.changeForm();
            break;
         }
         if (f == 4 && cB(a, 1, 1) && cB(c, 1, -1) && cB(d, 2, -2)) {
            MoveUp(form.a);
            MoveRight(form.a);
            MoveRight(form.c);
            MoveDown(form.c);
            MoveRight(form.d);
            MoveRight(form.d);
            MoveDown(form.d);
            MoveDown(form.d);
            form.changeForm();
            break;
         }
         break;
      case "l":
         if (f == 1 && cB(a, 1, -1) && cB(c, 1, 1) && cB(b, 2, 2)) {
            MoveRight(form.a);
            MoveDown(form.a);
            MoveUp(form.c);
            MoveRight(form.c);
            MoveUp(form.b);
            MoveUp(form.b);
            MoveRight(form.b);
            MoveRight(form.b);
            form.changeForm();
            break;
         }
         if (f == 2 && cB(a, -1, -1) && cB(b, 2, -2) && cB(c, 1, -1)) {
            MoveDown(form.a);
            MoveLeft(form.a);
            MoveRight(form.b);
            MoveRight(form.b);
            MoveDown(form.b);
            MoveDown(form.b);
            MoveRight(form.c);
            MoveDown(form.c);
            form.changeForm();
            break;
         }
         if (f == 3 && cB(a, -1, 1) && cB(c, -1, -1) && cB(b, -2, -2)) {
            MoveLeft(form.a);
            MoveUp(form.a);
            MoveDown(form.c);
            MoveLeft(form.c);
            MoveDown(form.b);
            MoveDown(form.b);
            MoveLeft(form.b);
            MoveLeft(form.b);
            form.changeForm();
            break;
         }
         if (f == 4 && cB(a, 1, 1) && cB(b, -2, 2) && cB(c, -1, 1)) {
            MoveUp(form.a);
            MoveRight(form.a);
            MoveLeft(form.b);
            MoveLeft(form.b);
            MoveUp(form.b);
            MoveUp(form.b);
            MoveLeft(form.c);
            MoveUp(form.c);
            form.changeForm();
            break;
         }
         break;
      case "o":
         break;
      case "s":
         if (f == 1 && cB(a, -1, -1) && cB(c, -1, 1) && cB(d, 0, 2)) {
            MoveDown(form.a);
            MoveLeft(form.a);
            MoveLeft(form.c);
            MoveUp(form.c);
            MoveUp(form.d);
            MoveUp(form.d);
            form.changeForm();
            break;
         }
         if (f == 2 && cB(a, 1, 1) && cB(c, 1, -1) && cB(d, 0, -2)) {
            MoveUp(form.a);
            MoveRight(form.a);
            MoveRight(form.c);
            MoveDown(form.c);
            MoveDown(form.d);
            MoveDown(form.d);
            form.changeForm();
            break;
         }
         if (f == 3 && cB(a, -1, -1) && cB(c, -1, 1) && cB(d, 0, 2)) {
            MoveDown(form.a);
            MoveLeft(form.a);
            MoveLeft(form.c);
            MoveUp(form.c);
            MoveUp(form.d);
            MoveUp(form.d);
            form.changeForm();
            break;
         }
         if (f == 4 && cB(a, 1, 1) && cB(c, 1, -1) && cB(d, 0, -2)) {
            MoveUp(form.a);
            MoveRight(form.a);
            MoveRight(form.c);
            MoveDown(form.c);
            MoveDown(form.d);
            MoveDown(form.d);
            form.changeForm();
            break;
         }
         break;
      case "t":
         if (f == 1 && cB(a, 1, 1) && cB(d, -1, -1) && cB(c, -1, 1)) {
            MoveUp(form.a);
            MoveRight(form.a);
            MoveDown(form.d);
            MoveLeft(form.d);
            MoveLeft(form.c);
            MoveUp(form.c);
            form.changeForm();
            break;
         }
         if (f == 2 && cB(a, 1, -1) && cB(d, -1, 1) && cB(c, 1, 1)) {
            MoveRight(form.a);
            MoveDown(form.a);
            MoveLeft(form.d);
            MoveUp(form.d);
            MoveUp(form.c);
            MoveRight(form.c);
            form.changeForm();
            break;
         }
         if (f == 3 && cB(a, -1, -1) && cB(d, 1, 1) && cB(c, 1, -1)) {
            MoveDown(form.a);
            MoveLeft(form.a);
            MoveUp(form.d);
            MoveRight(form.d);
            MoveRight(form.c);
            MoveDown(form.c);
            form.changeForm();
            break;
         }
         if (f == 4 && cB(a, -1, 1) && cB(d, 1, -1) && cB(c, -1, -1)) {
            MoveLeft(form.a);
            MoveUp(form.a);
            MoveRight(form.d);
            MoveDown(form.d);
            MoveDown(form.c);
            MoveLeft(form.c);
            form.changeForm();
            break;
         }
         break;
      case "z":
         if (f == 1 && cB(b, 1, 1) && cB(c, -1, 1) && cB(d, -2, 0)) {
            MoveUp(form.b);
            MoveRight(form.b);
            MoveLeft(form.c);
            MoveUp(form.c);
            MoveLeft(form.d);
            MoveLeft(form.d);
            form.changeForm();
            break;
         }
         if (f == 2 && cB(b, -1, -1) && cB(c, 1, -1) && cB(d, 2, 0)) {
            MoveDown(form.b);
            MoveLeft(form.b);
            MoveRight(form.c);
            MoveDown(form.c);
            MoveRight(form.d);
            MoveRight(form.d);
            form.changeForm();
            break;
         }
         if (f == 3 && cB(b, 1, 1) && cB(c, -1, 1) && cB(d, -2, 0)) {
            MoveUp(form.b);
            MoveRight(form.b);
            MoveLeft(form.c);
            MoveUp(form.c);
            MoveLeft(form.d);
            MoveLeft(form.d);
            form.changeForm();
            break;
         }
         if (f == 4 && cB(b, -1, -1) && cB(c, 1, -1) && cB(d, 2, 0)) {
            MoveDown(form.b);
            MoveLeft(form.b);
            MoveRight(form.c);
            MoveDown(form.c);
            MoveRight(form.d);
            MoveRight(form.d);
            form.changeForm();
            break;
         }
         break;
      case "i":
         if (f == 1 && cB(a, 2, 2) && cB(b, 1, 1) && cB(d, -1, -1)) {
            MoveUp(form.a);
            MoveUp(form.a);
            MoveRight(form.a);
            MoveRight(form.a);
            MoveUp(form.b);
            MoveRight(form.b);
            MoveDown(form.d);
            MoveLeft(form.d);
            form.changeForm();
            break;
         }
         if (f == 2 && cB(a, -2, -2) && cB(b, -1, -1) && cB(d, 1, 1)) {
            MoveDown(form.a);
            MoveDown(form.a);
            MoveLeft(form.a);
            MoveLeft(form.a);
            MoveDown(form.b);
            MoveLeft(form.b);
            MoveUp(form.d);
            MoveRight(form.d);
            form.changeForm();
            break;
         }
         if (f == 3 && cB(a, 2, 2) && cB(b, 1, 1) && cB(d, -1, -1)) {
            MoveUp(form.a);
            MoveUp(form.a);
            MoveRight(form.a);
            MoveRight(form.a);
            MoveUp(form.b);
            MoveRight(form.b);
            MoveDown(form.d);
            MoveLeft(form.d);
            form.changeForm();
            break;
         }
         if (f == 4 && cB(a, -2, -2) && cB(b, -1, -1) && cB(d, 1, 1)) {
            MoveDown(form.a);
            MoveDown(form.a);
            MoveLeft(form.a);
            MoveLeft(form.a);
            MoveDown(form.b);
            MoveLeft(form.b);
            MoveUp(form.d);
            MoveRight(form.d);
            form.changeForm();
            break;
         }
         break;
      }
   }

   private void RemoveRows(Pane pane) {
      ArrayList<Node> rects = new ArrayList<Node>();
      ArrayList<Integer> lines = new ArrayList<Integer>();
      ArrayList<Node> newrects = new ArrayList<Node>();
      int full = 0;
      for (int i = 0; i < MESH[0].length; i++) {
         for (int j = 0; j < MESH.length; j++) {
            if (MESH[j][i] == 1)
               full++;
         }
         if (full == MESH.length)
            lines.add(i);
         // lines.add(i + lines.size());
         full = 0;
      }
      if (lines.size() > 0)
         do {
            for (Node node : pane.getChildren()) {
               if (node instanceof Rectangle)
                  rects.add(node);
            }
            score += 50;
            linesNo++;

            for (Node node : rects) {
               Rectangle a = (Rectangle) node;
               if (a.getY() == lines.get(0) * SIZE) {
                  MESH[(int) a.getX() / SIZE][(int) a.getY() / SIZE] = 0;
                  pane.getChildren().remove(node);
               } else
                  newrects.add(node);
            }

            for (Node node : newrects) {
               Rectangle a = (Rectangle) node;
               if (a.getY() < lines.get(0) * SIZE) {
                  MESH[(int) a.getX() / SIZE][(int) a.getY() / SIZE] = 0;
                  a.setY(a.getY() + SIZE);
               }
            }
            lines.remove(0);
            rects.clear();
            newrects.clear();
            for (Node node : pane.getChildren()) {
               if (node instanceof Rectangle)
                  rects.add(node);
            }
            for (Node node : rects) {
               Rectangle a = (Rectangle) node;
               try {
                  MESH[(int) a.getX() / SIZE][(int) a.getY() / SIZE] = 1;
               } catch (ArrayIndexOutOfBoundsException e) {
               }
            }
            rects.clear();
         } while (lines.size() > 0);
   }

   private void MoveDown(Rectangle rect) {
      if (rect.getY() + MOVE < YMAX)
         rect.setY(rect.getY() + MOVE);

   }

   private void MoveRight(Rectangle rect) {
      if (rect.getX() + MOVE <= XMAX - SIZE)
         rect.setX(rect.getX() + MOVE);
   }

   private void MoveLeft(Rectangle rect) {
      if (rect.getX() - MOVE >= 0)
         rect.setX(rect.getX() - MOVE);
   }

   private void MoveUp(Rectangle rect) {
      if (rect.getY() - MOVE > 0)
         rect.setY(rect.getY() - MOVE);
   }

   private void MoveDown(Form form) {
      if (form.a.getY() == YMAX - SIZE || form.b.getY() == YMAX - SIZE || form.c.getY() == YMAX - SIZE
            || form.d.getY() == YMAX - SIZE || moveA(form) || moveB(form) || moveC(form) || moveD(form)) {
         MESH[(int) form.a.getX() / SIZE][(int) form.a.getY() / SIZE] = 1;
         MESH[(int) form.b.getX() / SIZE][(int) form.b.getY() / SIZE] = 1;
         MESH[(int) form.c.getX() / SIZE][(int) form.c.getY() / SIZE] = 1;
         MESH[(int) form.d.getX() / SIZE][(int) form.d.getY() / SIZE] = 1;
         RemoveRows(group);

         Form a = nextObj;
         nextObj = Controller.makeRect();
         object = a;
         group.getChildren().addAll(a.a, a.b, a.c, a.d);
         moveOnKeyPress(a);
      }

      if (form.a.getY() + MOVE < YMAX && form.b.getY() + MOVE < YMAX && form.c.getY() + MOVE < YMAX
            && form.d.getY() + MOVE < YMAX) {
         int movea = MESH[(int) form.a.getX() / SIZE][((int) form.a.getY() / SIZE) + 1];
         int moveb = MESH[(int) form.b.getX() / SIZE][((int) form.b.getY() / SIZE) + 1];
         int movec = MESH[(int) form.c.getX() / SIZE][((int) form.c.getY() / SIZE) + 1];
         int moved = MESH[(int) form.d.getX() / SIZE][((int) form.d.getY() / SIZE) + 1];
         if (movea == 0 && movea == moveb && moveb == movec && movec == moved) {
            form.a.setY(form.a.getY() + MOVE);
            form.b.setY(form.b.getY() + MOVE);
            form.c.setY(form.c.getY() + MOVE);
            form.d.setY(form.d.getY() + MOVE);
         }
      }
   }

   private boolean moveA(Form form) {
      return (MESH[(int) form.a.getX() / SIZE][((int) form.a.getY() / SIZE) + 1] == 1);
   }

   private boolean moveB(Form form) {
      return (MESH[(int) form.b.getX() / SIZE][((int) form.b.getY() / SIZE) + 1] == 1);
   }

   private boolean moveC(Form form) {
      return (MESH[(int) form.c.getX() / SIZE][((int) form.c.getY() / SIZE) + 1] == 1);
   }

   private boolean moveD(Form form) {
      return (MESH[(int) form.d.getX() / SIZE][((int) form.d.getY() / SIZE) + 1] == 1);
   }

   private boolean cB(Rectangle rect, int x, int y) {
      boolean xb = false;
      boolean yb = false;
      if (x >= 0)
         xb = rect.getX() + x * MOVE <= XMAX - SIZE;
      if (x < 0)
         xb = rect.getX() + x * MOVE >= 0;
      if (y >= 0)
         yb = rect.getY() - y * MOVE > 0;
      if (y < 0)
         yb = rect.getY() + y * MOVE < YMAX;
      return xb && yb && MESH[((int) rect.getX() / SIZE) + x][((int) rect.getY() / SIZE) - y] == 0;
   }

   void pause() {
      this.fall.cancel();
   }

   TimerTask makeTask() {
      TimerTask tempTask = new TimerTask() {
         public void run() {
            Platform.runLater(new Runnable() {

               public void run() {
                  System.out.println(top);
                  if (object.a.getY() == 0 || object.b.getY() == 0 || object.c.getY() == 0
                        || object.d.getY() == 0)
                     top++;
                  else
                     top = 0;

                  if (14 >= top && top >= 2) {
                     // GAME OVER
                     Text over = new Text("GAME OVER");
                     over.setFill(Color.RED);
                     over.setStyle("-fx-font: 70 arial;");
                     over.setY(250);
                     over.setX(10);
                     group.getChildren().add(over);

                     game = false;
                  }
                  if (top >= 15) {
                     task.cancel();
                     game = true;
                     dbSrv.updateScore(score);
                     score = 0;
                     top = 0;
                     linesNo = 0;
                     comSrv.WindowClose(group);
                  }
                  if (game) {
                     MoveDown(object);
                     scoretext.setText("Score: " + Integer.toString(score));
                     level.setText("Lines: " + Integer.toString(linesNo));
                  }
               }
            });
         }
      };
      return tempTask;

   }

   void resume() {
      this.fall = new Timer();
      task = makeTask();
      this.fall.schedule(task, 0, 100);
   }
}