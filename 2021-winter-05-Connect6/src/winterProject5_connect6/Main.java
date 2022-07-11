package winterProject5_connect6;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/*
 *정보
 2021-01-03-11:00~ 2021-01-04 ??:??
 2021-winter 과제5-1, 육목 게임환경 만들기 
 HGU 전산전자공학부_ 22100579 이진주
 with 김휘진
 */


public class Main extends JFrame {
   private static final long serialVersionUID = 1L;

// 자동실행, 바둑판 그리기
   @Override // 그냥...실행되는놈임
   public void paint(Graphics g) {
      super.paint(g);
      System.out.println("선긋는중...");

      for (int i = 0; i < 19; i++) {

         g.drawLine(30 + i * 40, 90, 30 + i * 40, 810);
         g.drawLine(30, 20 + i * 40 + 70, 750, 20 + i * 40 + 70);

         // 좌표라벨
         g.drawString(i + 1 + "", 25 + i * 40, 80); // 가로
         g.drawString(i + 1 + "", 13, 22 + i * 40 + 72); // 세로

         // 점
         if ((i == 3 || i == 9) || i == 15) {
            g.fillOval(30 + i * 40 - 3, 20 + 3 * 40 + 70 - 3, 6, 6);
            g.fillOval(30 + i * 40 - 3, 20 + 9 * 40 + 70 - 3, 6, 6);
            g.fillOval(30 + i * 40 - 3, 20 + 15 * 40 + 70 - 3, 6, 6);
         }
      }
   }

   static JPanel backGround; // 배경 그려지고 돌 놓아질 그곳
   static int x, y; // 현재좌표
   static Color COR; // 색
   static boolean on = false; // 게임중인가?(돌이 놓아지는가에 대한 어쩌구)
   static JPanel showTurn; // 배경색으로 차례 표시
   static JLabel Info; // 메세지 표시

   static int num; // 중립구 갯수!!!
   static int countJoong = 0;

   static int UserC = 1; // 유저가 선택한 컬러. 1 또는 2.
   static int ComC = 2;

   // 육목 판 만들기, 기본세팅
   public Main() {

      int width = 900, height = 900;
      // 기본껍데기
      setSize(width, height); // 프레임의 사이즈 설정
      setResizable(false);// 사용자가 임의로 프레임의 크기를 변경시킬 수 있는가>> 앙대
      setLocationRelativeTo(null);// 화면의 어느 위치에서 첫 등장할지>> null이면 자동 센터지정
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 창닫으면 프로그램종료
      setLayout(null); // 레이아웃설정

      // 백그라운드 패널(여기다 돌 둘거임)
      backGround = new JPanel();
      backGround.setBounds(0, 0, width, height);
      backGround.setBackground(new Color(200, 160, 100));
      backGround.setLayout(null);
      add(backGround); // 보여랏

      // 현재 차례 표시할 어쩌구
      Info = new JLabel("[System] 육목 게임을 시작합니다.");
      Info.setBounds(20, 0, 500, 30);
      backGround.add(Info);

      // 현재 차례 표시할 어쩌구
      JLabel turnInfo = new JLabel("당신의 색");
      turnInfo.setBounds(770, 50, 200, 50);
      backGround.add(turnInfo);

      showTurn = new JPanel();
      showTurn.setBounds(770, 90, 70, 70);
      showTurn.setBackground(Color.black);
      backGround.add(showTurn);

      // 다시하기
      JButton reset = new JButton("새로운 게임");
      reset.setBounds(760, 180, 100, 45);
      reset.setBackground(new Color(225, 215, 200));
      backGround.add(reset);
      reset.addActionListener(event -> {
         repaint(); // 게임판 리셋하고
         Info.setText("[System] 새로운 게임을 시작합니다.");
         countJoong = 0;
         for(int i = 0; i < 19; i++)
            for(int j = 0; j < 19; j++)
               alphago.weight[i][j] = 0;
         startGame(); // 새 게임 시작
      });

      // 예아~끗~
      JLabel info = new JLabel("2021-winter | java | project5 | Leeejjju");
      info.setBounds(665, 0, 225, 30);
      backGround.add(info);

      setVisible(true); // 쨘
      startGame();

      // 클릭시
      backGround.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent e) {
            if (on) { // 게임중일때만 작동하도록
               // 이상적인 위치선정(공식 만들어 사용함...)
               double n = ((float) e.getX() - (float) 44) / (float) 40;
               if (n < 0.05) {
                  x = 0;
               } else {
                  x = (int) n + 1;
               }
               n = ((float) e.getY() - (float) 80) / (float) 40;
               if (n < 0.05) {
                  y = 0;
               } else {
                  y = (int) n + 1;
               }

               if (countJoong < num) {

                  PlayBoard.count = -1; // 회색으로 염색시킨 중립구를
                  PlayBoard.putStone(Main.x, Main.y);
                  alphago.addWeight(x, y);
                  alphago.showWeight();
                  if (countJoong == num - 1) {
                     Info.setText("[System] 중립구 배치 완료. 게임을 시작합니다.");
                     if (UserC == 2) { // 유저가 백돌 선택했다면 흑돌 하나 미리 놓여지게
                        PlayBoard.count = 1; // 회색으로 염색시킨 중립구를
                        PlayBoard.putStone(9, 9);
                        alphago.addWeight(9, 9);
                        alphago.showWeight();
                     }
                  }

                  else
                     Info.setText("[System] " + (num - countJoong - 1) + "개의 중립구를 배치해주세요");
                  countJoong++;

               } else if (countJoong == num) { // 마지막 중립구 넣을 떄
                  PlayBoard.count = UserC; // 다음놓일 돌의 종류,갯수 조절
                  PlayBoard.setStone(); // 클릭위치에 중립구 두고
                  countJoong++;

               } else {
                  PlayBoard.setStone();
               }

            }

         }
      });

   }

   // 메인함수. 그저 실행할 뿐
   public static void main(String[] args) {
      System.out.println("Hello World!");
      new Main();

   }

   // 게임세팅
   static void startGame() {

      // 진영선택
      chooseColor(); // 색상선택
      countJoong = 0;
      PlayBoard.count = 0;
      // 중립구 놓기는 색 선택 끝나면 자동으로
      new PlayBoard();
      on = true; // 게임시쟉

   }

   // 진영 선택하기
   static void chooseColor() {

      JFrame f = new JFrame();
      f.setSize(300, 150); // 프레임의 사이즈 설정
      f.setResizable(false);// 사용자가 임의로 프레임의 크기를 변경시킬 수 있는가>> 앙대
      f.setLocationRelativeTo(null);// 화면의 어느 위치에서 첫 등장할지>> null이면 자동 센터지정
      f.setLayout(null); // 레이아웃설정

      JLabel info = new JLabel("      당신의 진영을 선택하세요");
      info.setBounds(5, 5, 280, 30);
      f.add(info);

      JCheckBox chooseBlcak = new JCheckBox("흑돌");
      chooseBlcak.setBounds(5, 40, 100, 30);
      f.add(chooseBlcak);
      JCheckBox chooseWhite = new JCheckBox("백돌");
      chooseWhite.setBounds(105, 40, 100, 30);
      f.add(chooseWhite);
      // 선택시 userC 변경되며 서로 상충되도록.
      chooseBlcak.addActionListener(event -> {
         chooseWhite.setSelected(false);
         UserC = 1;
         ComC = 2;
      });
      chooseWhite.addActionListener(event -> {
         chooseBlcak.setSelected(false);
         UserC = 2;
         ComC = 1;
      });

      JButton get = new JButton("confirm");
      get.setBounds(5, 77, 280, 30);
      get.setBackground(new Color(225, 215, 200));
      f.add(get);
      get.addActionListener(event -> {
         f.dispose();// 창닫고
         setBasicStone(); // 중립돌선택 화면으로
      });

      f.setVisible(true);

      // 게임세팅에도 넣어줘야함
   }

   // 중립구 갯수 선택하기
   static void setBasicStone() {

      JFrame f = new JFrame();
      f.setSize(300, 150); // 프레임의 사이즈 설정
      f.setResizable(false);// 사용자가 임의로 프레임의 크기를 변경시킬 수 있는가>> 앙대
      f.setLocationRelativeTo(null);// 화면의 어느 위치에서 첫 등장할지>> null이면 자동 센터지정
      f.setLayout(null); // 레이아웃설정

      JLabel info = new JLabel("      중립구의 갯수를 입력하세요");
      info.setBounds(5, 5, 280, 30);
      f.add(info);

      JTextField getNum = new JTextField();
      getNum.setBounds(5, 40, 280, 30);
      f.add(getNum);

      JButton get = new JButton("confirm");
      get.setBounds(5, 77, 280, 30);
      get.setBackground(new Color(225, 215, 200));
      f.add(get);
      get.addActionListener(event -> {

         // int count = 0;

         try {
            // 입력받은 숫자로 num세팅하기
            num = Integer.parseInt(getNum.getText());

            /*
             * //입력받은 갯수만큼의 난수위치에 중립구 놓기 while(count < n) { x = (int)(Math.random()*18);
             * //0~18범위의 난수 생성 y = (int)(Math.random()*18); //0~18범위의 난수 생성
             * if(PlayBoard.playBoard[x][y] == 0){ //빈자리라면 PlayBoard.count = -1; //회색으로 염색시킨
             * 중립구를 PlayBoard.putStone(); //난수위치에 둔다 count ++; } } PlayBoard.count = 1;
             * //다음차례때 흑돌 하나로 시작하도록 세팅
             */
            if (num != 0)
               Info.setText("[System] " + num + "개의 중립구를 배치해주세요");
            f.dispose(); // 창닫기

         } catch (Exception e) {
            JFrame pop = new JFrame(); // 팝업창
            JOptionPane.showMessageDialog(pop, "정수값을 입력해주세요");
         }

      });

      f.setVisible(true);

   }

   // 승리메세지
   static void winPopUp() {
      Info.setText("[system] " + "게임 종료! (승자: " + ((PlayBoard.c == 1) ? "흑돌)" : "백돌)"));
      JFrame pop = new JFrame(); // 팝업창
      JOptionPane.showMessageDialog(pop, "게임 종료! (승자: " + ((PlayBoard.c == 1) ? "흑돌)" : "백돌)"));
      on = false; // 게임끗
   }

}

//돌.... 시각적으로 돌이 놓일 위치좌표(픽셀기준)랑 내부적으로 인식될 좌표(배열 인덱스) 포함
class PlayBoard {
   // 게임판
   static int visualBoard[][][];
   static int playBoard[][]; // 내부

   PlayBoard() { // 초기설정.
      playBoard = new int[19][19]; // 0-18의 가로세로공간 부여
      visualBoard = new int[19][19][2]; // 0-18의 가로세로공간,

      for (int i = 0; i < 19; i++) {
         for (int j = 0; j < 19; j++) {
            playBoard[i][j] = 0;
            visualBoard[i][j][0] = 24 + i * 40; // x좌표
            visualBoard[i][j][1] = 60 + j * 40; // y좌표

         }
      }
   }

   static int c; // 돌 색깔..! 1이면 백돌 2면 흑돌 5면 거시기 그거 중립구
   static int count = 0; // 몇번쨰 두는건지

   static Graphics g = Main.backGround.getGraphics();

   // 돌 배치하기
   static void setStone() {
      if (Main.x < 19 && Main.y < 19) { // 범위 넘지 않는 선에서
         if (playBoard[Main.x][Main.y] == 0) {// 빈자리라면 돌 배치

            if (true) { // 원래 c!=5엿음
               Main.Info.setText("[system] (" + (Main.x + 1) + ", " + (Main.y + 1) + ") 에 "
                     + ((c == 1) ? "흑돌" : "백돌") + "을 배치했습니다\n");
               putStone(Main.x, Main.y); // 돌 놓기
               changeTurn(); // 현재차례 표시 바꾸기
            }

         } else {
            Main.Info.setText("[system] 이미 놓여진 자리입니다.");
         }
      }
   }

   // 돌 넣는 메서드
   static void putStone(int x, int y) {

      if (count == -1) { // 회색
         g.setColor(Color.gray);
         c = 5;
      } else if (count < 2) { // 흑돌
         c = 1;
         g.setColor(Color.black);
      } else { // 백돌
         c = 2;
         g.setColor(Color.white);
      }

      g.fillOval(PlayBoard.visualBoard[x][y][0] - 20, PlayBoard.visualBoard[x][y][1] - 20, 40, 40);
      playBoard[x][y] = c; // 돌 넣기
      if (ScanBoard.scan(Main.UserC)) { // 승리판정해서 게임 끝이라면
         Main.winPopUp(); // 팝업띄우기
         return;
      }else if (ScanBoard.scan(Main.ComC)) { // 승리판정해서 게임 끝이라면
         Main.winPopUp(); // 팝업띄우기
         return;
      }

      if (Main.UserC == c && count % 2 != 0) { // 방금 둔게 유저가 둔 수라면, 그리고 이게 두번째 두는거라면 (1, 3)
    	  alphago.addWeight(Main.x, Main.y); //유저가 둔 두번쨰 수 일반가중치 더하기 

         count++;
         if (count > 3) {
            count = 0;
         }
         
         //알파고가 수를 두기 직전에 현재 판의 특수가중치 계산하고 한번 보여줌 글고 수 두번 두기
         alphago.addSuperWeight();
         alphago.showWeight();
         
         alphago.returnPoint(alphago.weight);
         putStone(alphago.x, alphago.y); // 현재 산출되어있는 좌표에 돌 두기
         alphago.addWeight(alphago.x, alphago.y); //방금 둔거의 일반가중치 더하기
         
         if(!Main.on) {
        	 return;
         }
         
         alphago.returnPoint(alphago.weight);
         putStone(alphago.x, alphago.y); // 두번
         alphago.addWeight(alphago.x, alphago.y); //방금 둔거의 일반가중치 더하기
         


      } else {
         if (Main.UserC == c) {
            alphago.addWeight(Main.x, Main.y);
            alphago.showWeight();
         }

         count++;
         if (count > 3) {
            count = 0;
         }
      }

   }

   // 화면상 표시되는 패널색 바꾸기(인데 컴퓨터랑 할떄는 의미가 음슴)
   static void changeTurn() {

      if (count == -1) { // 회색
         // 블랙
      } else if (count < 2) { // 흑돌
         Main.showTurn.setBackground(Color.black);
      } else { // 백돌
         Main.showTurn.setBackground(Color.WHITE);
      }
   }

   // 인자로 받은 좌표위치 돌에 빨간 막 씌우는 메서드(승리구 표시용)
   static void markStone(int x, int y) {
      g.setColor(new Color(255, 0, 0, 80));
      PlayBoard.g.fillOval(PlayBoard.visualBoard[x][y][0] - 21, PlayBoard.visualBoard[x][y][1] - 21, 42, 42);
   }

}

//판정하는놈...!! 
class ScanBoard {

   static int x;
   static int y;
   static boolean flag = false; // 승리판정 및 반환용
   static int connect[] = { 1, 1, 1, 1 }; // 0세로 1가로 2우대각 3좌대각 연결점 갯수
   static int i; // 뭐...
   // 각각 인자로 받은 c의 연결점을 찾아 갯수를 connect에 저장한다.
   
   // 세로 연결점 세기(아래로/위로)
   static void sero(int c) {
      i = 1;
      while (true) {
         if (y + i > 18)
            break; // 인덱스 넘어갈라카면 스탑
         // System.out.println(x+","+y+"="+playBoard[x][y+i] );
         if (PlayBoard.playBoard[x][y + i] == c) {
            if (flag) { // 판정 난 상태라면 빨간테두ㄹㅣ 씌워주기
               PlayBoard.markStone(x, y + i);
            } else {
               connect[0]++; // 일치시 카운트 증가
            }
         } else
            break; // 불일치시 즉시스탑
         i++;
      }
      i = 1;
      while (true) {
         if (y - i < 0)
            break; // 인덱스 넘어갈라카면 스탑
         if (PlayBoard.playBoard[x][y - i] == c) {
            if (flag) { // 판정 난 상태라면 빨간테두ㄹㅣ 씌워주기
               PlayBoard.markStone(x, y - i);
            } else {
               connect[0]++; // 일치시 카운트 증가
            }
         } else
            break; // 불일치시 즉시스탑
         i++;
      }
      // System.out.println("세로연속점"+connect[0]+"개");

   }

   // 가로 연결점 세기
   static void garo(int c) {
      i = 1;
      while (true) {
         if (x + i > 18)
            break; // 인덱스 넘어갈라카면 스탑
         // System.out.println(x+","+y+"="+playBoard[x][y+i] );
         if (PlayBoard.playBoard[x + i][y] == c) {
            if (flag) { // 판정 난 상태라면 빨간테두ㄹㅣ 씌워주기
               PlayBoard.markStone(x + i, y);
            } else {
               connect[1]++; // 일치시 카운트 증가
            }
         } else
            break; // 불일치시 즉시스탑
         i++;
      }
      i = 1;
      while (true) {
         if (x - i < 0)
            break; // 인덱스 넘어갈라카면 스탑
         if (PlayBoard.playBoard[x - i][y] == c) {
            if (flag) { // 판정 난 상태라면 빨간테두ㄹㅣ 씌워주기
               PlayBoard.markStone(x - i, y);
            } else {
               connect[1]++; // 일치시 카운트 증가
            }
         } else
            break; // 불일치시 즉시스탑
         i++;
      }
      // System.out.println("가로연속점"+connect[1]+"개");

   }

   // 우대각 연결점 세기
   static void wo(int c) {
      i = 1;
      while (true) {
         if (y + i > 18 || x - i < 0)
            break; // 인덱스 넘어갈라카면 스탑
         // System.out.println(x+","+y+"="+playBoard[x][y+i] );
         if (PlayBoard.playBoard[x - i][y + i] == c) {
            if (flag) { // 판정 난 상태라면 빨간테두ㄹㅣ 씌워주기
               PlayBoard.markStone(x - i, y + i);
            } else {
               connect[2]++; // 일치시 카운트 증가
            }
         } else
            break; // 불일치시 즉시스탑
         i++;
      }
      i = 1;
      while (true) {
         if (x + i > 18 || y - i < 0)
            break; // 인덱스 넘어갈라카면 스탑
         if (PlayBoard.playBoard[x + i][y - i] == c) {
            if (flag) { // 판정 난 상태라면 빨간테두ㄹㅣ 씌워주기
               PlayBoard.markStone(x + i, y - i);
            } else {
               connect[2]++; // 일치시 카운트 증가
            }
         } else
            break; // 불일치시 즉시스탑
         i++;
      }
      // System.out.println("우대각연속점"+connect[2]+"개");

   }

   // 좌대각 연결점 세기
   static void jwa(int c) {
      i = 1;
      while (true) {
         if (y + i > 18 || x + i > 18)
            break; // 인덱스 넘어갈라카면 스탑
         // System.out.println(x+","+y+"="+playBoard[x][y+i] );
         if (PlayBoard.playBoard[x + i][y + i] == c) {
            if (flag) { // 판정 난 상태라면 빨간테두ㄹㅣ 씌워주기
               PlayBoard.markStone(x + i, y + i);
            } else {
               connect[3]++; // 일치시 카운트 증가
            }
         } else
            break; // 불일치시 즉시스탑
         i++;
      }
      i = 1;
      while (true) {
         if (x - i < 0 || y - i < 0)
            break; // 인덱스 넘어갈라카면 스탑
         if (PlayBoard.playBoard[x - i][y - i] == c) {
            if (flag) { // 판정 난 상태라면 빨간테두ㄹㅣ 씌워주기
               PlayBoard.markStone(x - i, y - i);
            } else {
               connect[3]++; // 일치시 카운트 증가
            }
         } else
            break; // 불일치시 즉시스탑
         i++;
      }
      // System.out.println("좌대각연속점"+connect[3]+"개");

   }

   // 여섯개짜리 연결점이 있는지 판정
   static boolean scan(int c) {
      flag = false;
      // 현시점의 x,y 받아오기
      if (Main.UserC == c) {
         x = Main.x;
         y = Main.y;

      } else {
         x = alphago.x;
         y = alphago.y;
      }
      for (i = 0; i < 4; i++) { // 초기화
         connect[i] = 1;
      }
      // 팔방으로 뒤지기..!
      sero(c);
      garo(c);
      wo(c);
      jwa(c);

      // 판별
      for (i = 0; i < 4; i++) {
         if (connect[i] == 6) {
            flag = true;
            System.out.println("승리");
            // 빨갛게 씌우기..!!
            PlayBoard.markStone(x, y);
            if (i == 0) { // 세로..
               sero(c);
               return flag;
            } else if (i == 1) { // 가로...
               garo(c);
               return flag;
            } else if (i == 2) {// 우대각..
               wo(c);
               return flag;
            } else { // 좌대각..
               jwa(c);
               return flag;
            }
         }
      }
      return flag;
   }

}

//컴퓨터가 하는 일 
class alphago {

   // 현재 산출된 이상적 좌표
   static int x;
   static int y;
   // 가중치 설정을 위한 배열
   static int[][] weight = new int[19][19]; //일반가중치(계속 누적)
   static int[][] superWeight = new int[19][19]; //특수가중치(매 분석마다 리셋후 시작)

   // 가중치 기본 누적하기 (매 실행 후에)
   public static void addWeight(int x, int y) {
      int n = 1; //누적할 가중치의 양. 내 돌인지 상대 돌인지에 따라 달라짐. 
      
      if (PlayBoard.playBoard[x][y] == 1) n = 2;
      else if (PlayBoard.playBoard[x][y] == 2) n = 1;
      else if (PlayBoard.playBoard[x][y] == 5) { //중립구 취급
         weight[x][y] = -1;
         superWeight[x][y] = -1;
         return;
      } else  return; //이미 놓여진곳 취급 
      
      //팔방을 뒤져서 이미 놓여진 곳만 아니라면 가중치 누적. 
      for (int i = x - 1; i < x + 2; i++) {
         for (int j = y - 1; j < y + 2; j++) {
            if (i == x && j == y) { //본인자리에는 -1 
               weight[x][y] = -1;
               superWeight[x][y] = -1;
            } else {
               try {
                  if (PlayBoard.playBoard[i][j] == 0) weight[i][j] += n;
               } catch (ArrayIndexOutOfBoundsException e) {} //인덱스 넘어서면 무시 
            }
         }
      }
      
   }


   
   //판 읽고 특수가중치 누적하기 
   public static void addSuperWeight() {
	   
	   //특수가중치 판 초기화 
	   for(int i = 0; i < 19; i++) {
		   for(int j = 0; j < 19; j++) {
			   superWeight[i][j] = 0;
		   }
	   }

	   int myCount = 0;
	   int add = 0; //현재 가중치 몇번 누적했는지(두번 넘어가면 그만찾고 리턴. 어차피 한 턴에 두번밖에 못 두니까..)
	     
	   //// 놓으면 이길 때(한방승리) -------------------------------------------------------------------------------------
	   
	   
	   //세로 시작점 ----------------------------------------------------------------------
	      
	   if (add >= 2)
			return;
		// 공격/세로/5-1
		for (int i = 0; i < 19; i++) {
			myCount = 0;
			for (int j = 0; j < 19; j++) {
				try {
					if (PlayBoard.playBoard[i][j] == Main.ComC) {
						myCount++;
						if (myCount == 5) {
							// 양끝 중 아무 빈곳에 가중치 왕창 이벤트
							/*
							 * if (j - 5 < 0 && PlayBoard.playBoard[i][j + 1] == 0) { // |[11111]0 right
							 * superWeight[i][j + 1] += 40; add++; } else if (j + 1 > 18 &&
							 * PlayBoard.playBoard[i][j - 5] == 0) { // 0[11111]| left superWeight[i][j - 5]
							 * += 40; add++; } else
							 */ if (j - 5 > 0 && PlayBoard.playBoard[i][j - 5] == 0) { // 0[11111] left //made a change
								superWeight[i][j - 5] += 80;
								add++;
							} else if (j + 1 < 19 && PlayBoard.playBoard[i][j + 1] == 0) { // *[11111]0 right //also
																							// made a change
								superWeight[i][j + 1] += 80;
								add++;
							}

						}
					} else
						myCount = 0;
				} catch (ArrayIndexOutOfBoundsException e) {
				}
			}
		}

		if (add >= 2)
			return;
		// 공격/세로/1-4-1/2-4
		for (int i = 0; i < 19; i++) {
			myCount = 0;
			for (int j = 0; j < 19; j++) {
				try {
					if (PlayBoard.playBoard[i][j] == Main.ComC) {
						myCount++;

						if (myCount == 4) {

							/*
							 * if (j - 4 < 0 && PlayBoard.playBoard[i][j + 1] == 0 &&
							 * PlayBoard.playBoard[i][j + 2] == 0) { // |[1111]00 right superWeight[i][j +
							 * 1] += 40; superWeight[i][j + 2] += 40; add += 2; } else if (j + 1 > 18 &&
							 * PlayBoard.playBoard[i][j - 4] == 0 && PlayBoard.playBoard[i][j - 5] == 0) {
							 * // 00[1111]| left superWeight[i][j - 4] += 40; superWeight[i][j - 5] += 40;
							 * add += 2; } else
							 */ if (j - 4 > 0 && j + 1 < 19 && PlayBoard.playBoard[i][j - 4] == 0
									&& PlayBoard.playBoard[i][j + 1] == 0) { // 0[1111]0 mid
								superWeight[i][j - 4] += 80;
								superWeight[i][j + 1] += 80;
								add += 2;
							}

							else if (j - 5 < 0 && PlayBoard.playBoard[i][j - 4] == 0
									&& PlayBoard.playBoard[i][j - 5] == 0) { // 00[1111] left ??????????????
								superWeight[i][j - 4] += 80;
								superWeight[i][j - 5] += 80;
								add += 2;
							} else if (j + 2 < 19 && PlayBoard.playBoard[i][j + 1] == 0
									&& PlayBoard.playBoard[i][j + 2] == 0) { // [1111]00 right
								superWeight[i][j + 2] += 80;
								superWeight[i][j + 1] += 80;
								add += 2;
							}

						}
					} else
						myCount = 0;
				} catch (ArrayIndexOutOfBoundsException e) {
				}
			}
		}

		if (add >= 2)
			return;
		// 공격/세로/3-1-1-1/1-3-1-1
		for (int i = 0; i < 19; i++) {
			myCount = 0;
			for (int j = 0; j < 19; j++) {
				try {
					if (PlayBoard.playBoard[i][j] == Main.ComC) {
						myCount++;
						if (myCount == 3) { // 세번연속일때

							if (j + 3 < 19 && PlayBoard.playBoard[i][j + 1] == 0
									&& PlayBoard.playBoard[i][j + 2] == Main.ComC
									&& PlayBoard.playBoard[i][j + 3] == 0) { // [111]010 right

								superWeight[i][j + 1] += 40;
								superWeight[i][j + 3] += 40;
								add += 2;
							}

							else if (j - 5 > 0
									&& (PlayBoard.playBoard[i][j - 3] == 0 && PlayBoard.playBoard[i][j - 4] == Main.ComC
											&& PlayBoard.playBoard[i][j - 5] == 0)) { // 010[111] left

								superWeight[i][j - 3] += 40;
								superWeight[i][j - 5] += 40;
								add += 2;
							}

							else if (j - 3 > 0 && j + 2 < 19 && PlayBoard.playBoard[i][j - 3] == 0
									&& PlayBoard.playBoard[i][j + 1] == 0
									&& PlayBoard.playBoard[i][j + 2] == Main.ComC) { // 0[111]01 mid
								superWeight[i][j - 3] += 40;
								superWeight[i][j + 2] += 40;
								add += 2;
							} else if (j - 4 > 0 && j + 1 < 19 && PlayBoard.playBoard[i][j - 3] == 0
									&& PlayBoard.playBoard[i][j - 4] == Main.ComC
									&& PlayBoard.playBoard[i][j + 1] == 0) { // 10[111]0 mid
								superWeight[i][j - 3] += 40;
								superWeight[i][j + 1] += 40;
								add += 2;
							}
						} else
							myCount = 0;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
				}
			}
		}

		if (add >= 2)
			return;
		// 공격/세로/3-2-1
		for (int i = 0; i < 19; i++) {
			myCount = 0;
			for (int j = 0; j < 19; j++) {
				try {
					if (PlayBoard.playBoard[i][j] == Main.ComC) {
						myCount++;
						if (myCount == 3) { // 세번연속일때

							if (j + 3 < 19 && (PlayBoard.playBoard[i][j + 1] == 0 && PlayBoard.playBoard[i][j + 2] == 0
									&& PlayBoard.playBoard[i][j + 3] == Main.ComC)) { // [111]001 right

								superWeight[i][j + 1] += 40;
								superWeight[i][j + 2] += 40;
								add += 2;
							}

							else if (j - 5 > 0
									&& (PlayBoard.playBoard[i][j - 3] == 0 && PlayBoard.playBoard[i][j - 4] == 0
											&& PlayBoard.playBoard[i][j - 5] == Main.ComC)) { // 100[111] left

								superWeight[i][j - 3] += 40;
								superWeight[i][j - 4] += 40;
								add += 2;

							}
						} else
							myCount = 0;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
				}
			}
		}

		if (add >= 2)
			return;
		// 공격/세로/2-2-2/1-2-1-2/1-1-1-1-2/1-1-2-1-1
		for (int i = 0; i < 19; i++) {
			myCount = 0;
			for (int j = 0; j < 19; j++) {
				// try { WHY????????????
				if (PlayBoard.playBoard[i][j] == Main.ComC) {
					myCount++;
					if (myCount == 2) {

						try {

							if (j + 4 < 19 && PlayBoard.playBoard[i][j + 1] == 0 && PlayBoard.playBoard[i][j + 2] == 0
									&& PlayBoard.playBoard[i][j + 3] == Main.ComC
									&& PlayBoard.playBoard[i][j + 4] == Main.ComC) { // [11]0011 right
								superWeight[i][j + 1] += 40;
								superWeight[i][j + 2] += 40;
								add += 2;
							} /*
								 * else if (j - 5 > 0 && PlayBoard.playBoard[i][j - 2] == 0 &&
								 * PlayBoard.playBoard[i][j - 3] == 0 && PlayBoard.playBoard[i][j - 4] ==
								 * Main.ComC && PlayBoard.playBoard[i][j - 5] == Main.ComC) { // 1100[11] left
								 * superWeight[i][j - 2] += 40; superWeight[i][j - 3] += 40; add += 2; }
								 */ else if (j - 2 > 0 && j + 3 > 19 && PlayBoard.playBoard[i][j - 2] == 0
									&& PlayBoard.playBoard[i][j + 1] == 0 && PlayBoard.playBoard[i][j + 2] == Main.ComC
									&& PlayBoard.playBoard[i][j + 3] == Main.ComC) { // 0[11]011 mid
								superWeight[i][j - 2] += 40;
								superWeight[i][j + 1] += 40;
								add += 2;
							} /*
								 * else if (j - 5 > 0 && PlayBoard.playBoard[i][j - 2] == 0 &&
								 * PlayBoard.playBoard[i][j - 5] == 0 && PlayBoard.playBoard[i][j - 3] ==
								 * Main.UserC && PlayBoard.playBoard[i][j - 4] == Main.ComC) { // 0110[11] left
								 * superWeight[i][j - 2] += 40; superWeight[i][j - 5] += 40; add += 2; }
								 */ else if (j - 5 > 0 && PlayBoard.playBoard[i][j - 2] == 0
									&& PlayBoard.playBoard[i][j - 4] == 0 && PlayBoard.playBoard[i][j - 3] == Main.ComC
									&& PlayBoard.playBoard[i][j - 5] == Main.ComC) { // 1010[11] left
								superWeight[i][j - 2] += 40;
								superWeight[i][j - 4] += 40;
								add += 2;
							} else if (j - 3 > 0 && j + 2 < 19 && PlayBoard.playBoard[i][j - 2] == 0
									&& PlayBoard.playBoard[i][j + 1] == 0 && PlayBoard.playBoard[i][j - 3] == Main.ComC
									&& PlayBoard.playBoard[i][j + 2] == Main.ComC) { // 10[11]01 mid
								superWeight[i][j - 2] += 40;
								superWeight[i][j + 1] += 40;
								add += 2;
							} else if (j + 4 < 19 && PlayBoard.playBoard[i][j + 1] == 0
									&& PlayBoard.playBoard[i][j + 3] == 0 && PlayBoard.playBoard[i][j + 2] == Main.ComC
									&& PlayBoard.playBoard[i][j + 4] == Main.ComC) { // [11]0101 right
								superWeight[i][j + 3] += 40;
								superWeight[i][j + 1] += 40;
								add += 2;
							} else if (j + 4 < 19 && PlayBoard.playBoard[i][j + 1] == 0
									&& PlayBoard.playBoard[i][j + 4] == 0 && PlayBoard.playBoard[i][j + 2] == Main.ComC
									&& PlayBoard.playBoard[i][j + 3] == Main.ComC) { // [11]0110 right
								superWeight[i][j + 4] += 40;
								superWeight[i][j + 1] += 40;
								add += 2;
							} /*
								 * else if (j - 4 > 0 && j + 1 < 19 && PlayBoard.playBoard[i][j + 1] == 0 &&
								 * PlayBoard.playBoard[i][j - 2] == 0 && PlayBoard.playBoard[i][j - 3] ==
								 * Main.ComC && PlayBoard.playBoard[i][j - 4] == Main.ComC) { // 110[11]0 mid
								 * superWeight[i][j - 2] += 40; superWeight[i][j + 1] += 40; add += 2; }
								 */
						} catch (ArrayIndexOutOfBoundsException e) {}
					}
				} else myCount = 0;
				// }catch (ArrayIndexOutOfBoundsException e) {}
			}
		}

	      
	
		//가로 시작점 -------------------------------------------------------------------------------------------------

		if (add >= 2)
			return;
		// 공격/가로/5-1
		for (int j = 0; j < 19; j++) {
			myCount = 0;
			for (int i = 0; i < 19; i++) {
				try {
					if (PlayBoard.playBoard[i][j] == Main.ComC) {
						myCount++;
						if (myCount == 5) { // 양끝 중 아무 빈곳에 가중치 왕창 이벤트
							/*
							 * if (i - 5 < 0 && PlayBoard.playBoard[i + 1][j] == 0) { // |[11111]0 (right) ?
							 * superWeight[i + 1][j] += 40; add++; }
							 * 
							 * else if (i + 1 > 18 && PlayBoard.playBoard[i - 5][j] == 0) { // 0[11111]|
							 * (left) ? superWeight[i - 5][j] += 40; add++; }
							 */

							/* else */ if (i - 5 > 0 && PlayBoard.playBoard[i - 5][j] == 0) { // 0[11111] (left)
								superWeight[i - 5][j] += 80;
								add++;
							}

							else if (i + 1 < 19 && PlayBoard.playBoard[i + 1][j] == 0) { // *[11111]0 (right)
								superWeight[i + 1][j] += 80;
								add++;
							}
						}
					} else
						myCount = 0;
				} catch (ArrayIndexOutOfBoundsException e) {
				}
			}
		}

		if (add >= 2)
			return;
		// 공격/가로/4-2/1-4-1
		for (int j = 0; j < 19; j++) {
			myCount = 0;
			for (int i = 0; i < 19; i++) {
				try {
					if (PlayBoard.playBoard[i][j] == Main.ComC) {
						myCount++;
						if (myCount == 4) {

							/*
							 * if (i - 4 < 0 && PlayBoard.playBoard[i + 1][j] == 0 && PlayBoard.playBoard[i
							 * + 2][j] == 0) { // |[1111]00 right //? superWeight[i + 1][j] += 40;
							 * superWeight[i + 2][j] += 40; add += 2; }
							 * 
							 * else if (i + 1 > 18 && PlayBoard.playBoard[i - 5][j] == 0 &&
							 * PlayBoard.playBoard[i - 4][j] == 0) { // 00[1111]| //(left) //? superWeight[i
							 * - 5][j] += 40; superWeight[i - 4][j] += 40; add += 2; }
							 */

							/* else */ if (i - 4 > 0 && i + 1 < 19 && PlayBoard.playBoard[i - 4][j] == 0
									&& PlayBoard.playBoard[i + 1][j] == 0) { // 0[1111]0 (mid)
								superWeight[i - 4][j] += 80;
								superWeight[i + 1][j] += 80;
								add += 2;
							}

							if (i - 5 > 0 && PlayBoard.playBoard[i - 4][j] == 0 && PlayBoard.playBoard[i - 5][j] == 0) { // 00[1111]
																															// left
								superWeight[i - 4][j] += 80;
								superWeight[i - 5][j] += 80;
								add += 2;
							} else if (i + 2 < 19 && PlayBoard.playBoard[i + 1][j] == 0
									&& PlayBoard.playBoard[i + 2][j] == 0) { // [1111]00 right
								superWeight[i + 2][j] += 80;
								superWeight[i + 1][j] += 80;
								add += 2;
							}

						}
					} else
						myCount = 0;
				} catch (ArrayIndexOutOfBoundsException e) {
				}
			}
		}

		if (add >= 2)
			return;
		// 공격/가로/3-1-1-1
		for (int j = 0; j < 19; j++) {
			myCount = 0;
			for (int i = 0; i < 19; i++) {
				try {
					if (PlayBoard.playBoard[i][j] == Main.ComC) {
						myCount++;
						if (myCount == 3) {
							if (i + 3 < 19 && PlayBoard.playBoard[i + 1][j] == 0
									&& PlayBoard.playBoard[i + 2][j] == Main.ComC
									&& PlayBoard.playBoard[i + 3][j] == 0) { // [111]010 right

								superWeight[i + 3][j] += 40;
								superWeight[i + 1][j] += 40;
								add += 2;
							} else if (i - 5 > 0 && PlayBoard.playBoard[i - 3][j] == 0
									&& PlayBoard.playBoard[i - 4][j] == Main.ComC
									&& PlayBoard.playBoard[i - 5][j] == 0) { // 010[111] left

								superWeight[i - 3][j] += 40;
								superWeight[i - 5][j] += 40;
								add += 2;
							}
						}
					} else
						myCount = 0;
				} catch (ArrayIndexOutOfBoundsException e) {
				}
			}
		}

		if (add >= 2)
			return;
		// 공격/가로/3-2-1/1-3-1-1
		for (int j = 0; j < 19; j++) {
			myCount = 0;
			for (int i = 0; i < 19; i++) {
				try {
					if (PlayBoard.playBoard[i][j] == Main.ComC) {
						myCount++;
						if (myCount == 3) {
							if (i + 3 < 19 && PlayBoard.playBoard[i + 1][j] == 0 && PlayBoard.playBoard[i + 2][j] == 0
									&& PlayBoard.playBoard[i + 3][j] == Main.ComC) { // [111]001 right
								superWeight[i + 1][j] += 40;
								superWeight[i + 2][j] += 40;
								add += 2;
							} else if (i - 5 > 0 && PlayBoard.playBoard[i - 3][j] == 0
									&& PlayBoard.playBoard[i - 4][j] == 0
									&& PlayBoard.playBoard[i - 5][j] == Main.ComC) {// 100[111] left

								superWeight[i - 3][j] += 40;
								superWeight[i - 4][j] += 40;
								add += 2;
							}
							
							else if (i - 3 > 0 && i + 2 < 19 && PlayBoard.playBoard[i - 3][j] == 0
									&& PlayBoard.playBoard[i + 1][j] == 0
									&& PlayBoard.playBoard[i + 2][j] == Main.ComC) { // 0[111]01 mid
								superWeight[i - 3][j] += 40;
								superWeight[i + 2][j] += 40;
								add += 2;
							} else if (i - 4 > 0 && i + 1 < 19 && PlayBoard.playBoard[i - 3][j] == 0
									&& PlayBoard.playBoard[i - 4][j] == Main.ComC
									&& PlayBoard.playBoard[i + 1][j] == 0) { // 10[111]0 mid
								superWeight[i - 3][j] += 40;
								superWeight[i + 1][j] += 40;
								add += 2;
							}
						}
					} else
						myCount = 0;
				} catch (ArrayIndexOutOfBoundsException e) {
				}
			}
		}

		if (add >= 2)
			return;
		// 공격/가로/2-2-2/1-2-1-2/1-1-1-1-2/1-1-2-1-1
		for (int j = 0; j < 19; j++) {
			myCount = 0; // initialize myCount when entering new row
			for (int i = 0; i < 19; i++) {
				try {
					if (PlayBoard.playBoard[i][j] == Main.ComC) {
						myCount++;
						if (myCount == 2) {
							if (i + 4 < 19 && PlayBoard.playBoard[i + 1][j] == 0 && PlayBoard.playBoard[i + 2][j] == 0
									&& PlayBoard.playBoard[i + 3][j] == Main.ComC
									&& PlayBoard.playBoard[i + 4][j] == Main.ComC) { // [11]0011 (right)
								superWeight[i + 1][j] += 40;
								superWeight[i + 2][j] += 40;
								add += 2;
							}
							/*
							 * else if (i - 5 > 0 && PlayBoard.playBoard[i - 2][j] == 0 &&
							 * PlayBoard.playBoard[i - 3][j] == 0 && PlayBoard.playBoard[i - 4][j] ==
							 * Main.ComC && PlayBoard.playBoard[i - 5][j] == Main.ComC) { // 1100[11] left
							 * // same as above superWeight[i - 2][j] += 40; superWeight[i - 3][j] += 40;
							 * add += 2; }
							 */
							else if (i - 2 > 0 && i + 3 < 19 && PlayBoard.playBoard[i - 2][j] == 0
									&& PlayBoard.playBoard[i + 1][j] == 0 && PlayBoard.playBoard[i + 2][j] == Main.ComC
									&& PlayBoard.playBoard[i + 3][j] == Main.ComC) { // 0[11]011 (mid)
								superWeight[i - 2][j] += 40;
								superWeight[i + 1][j] += 40;
								add += 2;
							}

							/*
							 * else if (i - 5 > 0 && PlayBoard.playBoard[i - 2][j] == 0 &&
							 * PlayBoard.playBoard[i - 5][j] == 0 && PlayBoard.playBoard[i - 3][j] ==
							 * Main.ComC && PlayBoard.playBoard[i - 4][j] == Main.ComC) { // 0110[11] (left)
							 * // same as above superWeight[i - 2][j] += 40; superWeight[i - 5][j] += 40;
							 * add += 2; }
							 */

							else if (i - 5 > 0 && PlayBoard.playBoard[i - 2][j] == 0
									&& PlayBoard.playBoard[i - 4][j] == 0 && PlayBoard.playBoard[i - 3][j] == Main.ComC
									&& PlayBoard.playBoard[i - 5][j] == Main.ComC) { // 1010[11] (left)
								superWeight[i - 2][j] += 40;
								superWeight[i - 4][j] += 40;
								add += 2;
							}

							else if (i - 3 > 0 && i + 2 < 19 && PlayBoard.playBoard[i - 2][j] == 0
									&& PlayBoard.playBoard[i + 1][j] == 0 && PlayBoard.playBoard[i - 3][j] == Main.ComC
									&& PlayBoard.playBoard[i + 2][j] == Main.ComC) { // 10[11]01 mid
								superWeight[i - 2][j] += 40;
								superWeight[i + 1][j] += 40;
								add += 2;
							}

							else if (i + 4 < 19 && PlayBoard.playBoard[i + 1][j] == 0
									&& PlayBoard.playBoard[i + 3][j] == 0 && PlayBoard.playBoard[i + 2][j] == Main.ComC
									&& PlayBoard.playBoard[i + 4][j] == Main.ComC) { // [11]0101 (right)
								superWeight[i + 3][j] += 40;
								superWeight[i + 1][j] += 40;
								add += 2;
							}

							else if (i + 4 < 19 && PlayBoard.playBoard[i + 1][j] == 0
									&& PlayBoard.playBoard[i + 4][j] == 0 && PlayBoard.playBoard[i + 2][j] == Main.ComC
									&& PlayBoard.playBoard[i + 3][j] == Main.ComC) { // [11]0110 (right)
								superWeight[i + 4][j] += 40;
								superWeight[i + 1][j] += 40;
								add += 2;
							}

							/*
							 * else if (i - 4 > 0 && i + 1 < 19 && PlayBoard.playBoard[i + 1][j] == 0 &&
							 * PlayBoard.playBoard[i - 2][j] == 0 && PlayBoard.playBoard[i - 3][j] ==
							 * Main.ComC && PlayBoard.playBoard[i - 4][j] == Main.ComC) { // 110[11]0 (mid)
							 * //same as above superWeight[i - 2][j] += 40; superWeight[i + 1][j] += 40;
							 * add++; }
							 */
						}
					} else
						myCount = 0;
				} catch (ArrayIndexOutOfBoundsException e) {
				}
			}
		}
	         
		//좌대각 시작점 ----------------------------------------------------------------------

	      
	      
	      if(add >= 2) return;
	   // 5 왼쪽 위에서 오른쪽 아래(좌대각\) 공격 
	      for (int i = 0; i < 19; i++) {
	         myCount = 0;
	         for (int j = 0; j < 19; j++) {
	            int temp1 = i;
	            int temp2 = j;
	            for (int k = 0; k < 5; k++) {
	            	try {
	                if (PlayBoard.playBoard[temp1][temp2] == Main.ComC) {
	                  myCount++;
	                     if (myCount == 5) {
	                    	if((temp1 - 5 < 0 || temp2 - 5 < 0) && PlayBoard.playBoard[temp1 + 1][temp2 + 1] == 0) {
								superWeight[temp1 + 1][temp2 + 1] += 40; add++;
							}else if((temp1 + 1 > 18 || temp2 + 1 > 18) && PlayBoard.playBoard[temp1 - 5][temp2 - 5] == 0){
								superWeight[temp1 - 5][temp2 - 5] += 40; add++;
							}else if ((temp1 - 5 < 0 || temp2 - 5 < 0) && PlayBoard.playBoard[temp1 - 5][temp2 - 5] == 0) {
	                        	superWeight[temp1 - 5][temp2 - 5] += 80; add++;
	                        }else if ((temp1 + 1 > 18 || temp2 + 1 > 18) && PlayBoard.playBoard[temp1 + 1][temp2 + 1] == 0) {
	                        	superWeight[temp1 + 1][temp2 + 1] += 80; add++;
	                        } 
	                     }
	                  temp1++;
	                  temp2++;
	               } else myCount = 0;
	               } catch (ArrayIndexOutOfBoundsException e) {}
	            }
	         }
	      }
	      
	      
	      
	      if(add >= 2) return;
	      // 4 왼쪽 위에서 오른쪽 아래(좌대각\) 공격
	      for (int i = 0; i < 19; i++) {
	         myCount = 0;
	         for (int j = 0; j < 19; j++) {
	            int temp1 = i;
	            int temp2 = j;
	            for (int k = 0; k < 4; k++) {
	            	try {
		                if (PlayBoard.playBoard[temp1][temp2] == Main.ComC) {
		                  myCount++;
		                     if (myCount == 4) {
		                    	 if((temp1 - 4 <= 0 || temp2 - 4 <= 0) && (temp1 + 2 <= 18 && temp2 +2 <= 18) && PlayBoard.playBoard[temp1 + 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 + 2][temp2 + 2] == 0) {
									superWeight[temp1 + 1][temp2 + 1] += 40; add++;
									superWeight[temp1 + 2][temp2 + 2] += 40; add++;
								}else if((temp1 + 1 >= 18 || temp2 + 1 >= 18) && (temp1 - 5 >= 0 && temp2 - 5 >= 0) && PlayBoard.playBoard[temp1 - 5][temp2 - 5] == 0 && PlayBoard.playBoard[temp1 - 4][temp2 - 4] == 0){
									superWeight[temp1 - 5][temp2 - 5] += 40; add++;
									superWeight[temp1 - 4][temp2 - 4] += 40; add++;
								}else if ((temp1 -4 > 0 && temp2 -4 > 0) && (temp1 + 1 < 19 && temp2 + 1 < 19) && PlayBoard.playBoard[temp1 - 4][temp2 - 4] == 0 && PlayBoard.playBoard[temp1 + 1][temp2 + 1] == 0) {
		                        	superWeight[temp1 - 4][temp2 - 4] += 80; add++;
		                        	superWeight[temp1 + 1][temp2 + 1] += 80; add++;
		                        } else if ((temp1 - 5 >= 0 && temp2 - 5 >= 0) && (PlayBoard.playBoard[temp1 - 4][temp2 - 4] == 0 && PlayBoard.playBoard[temp1 - 5][temp2 - 5] == 0)) {
	                        	   	superWeight[temp1 - 4][temp2 - 4] += 80; add++;
	                        	   	superWeight[temp1 - 5][temp2 - 5] += 80; add++;
	                           } else if ((temp1 + 2 <= 18 && temp2 +2 <= 18) && (PlayBoard.playBoard[temp1 + 1][temp2 + 1] == 0&& PlayBoard.playBoard[temp1 + 2][temp2 + 2] == 0)) {
	                        	    superWeight[temp1 + 1][temp2 + 1] += 80; add++;
	                        	    superWeight[temp1 + 2][temp2 + 2] += 80; add++;
	                           }
		                     }
		                  temp1++;
		                  temp2++;
		               } else myCount = 0;
	               } catch (ArrayIndexOutOfBoundsException e) {}
	            }
	         }
	      }
	      

		   if(add >= 2) return;
		   // 3(좌대각\) 1공백 공격
		   for(int i = 0;i<19;i++){
		      myCount = 0;
		      for (int j = 0; j < 19; j++) {
		         int temp1 = i;
		         int temp2 = j;
		         for (int k = 0; k < 3; k++) {
		        	 try {
			             if (PlayBoard.playBoard[temp1][temp2] == Main.ComC) {
			               myCount++;
			                  if (myCount == 3) {
			                    if ((temp1 + 3 <= 18 && temp2 + 3 <= 18) && PlayBoard.playBoard[temp1 + 1][temp2+ 1] == 0 && PlayBoard.playBoard[temp1 + 2][temp2 + 2] == Main.ComC && PlayBoard.playBoard[temp1 + 3][temp2+ 3] == 0) {
									superWeight[temp1 + 1][temp2+ 1] += 40; add++;
			                    	superWeight[temp1 + 3][temp2+ 3] += 40; add++;
			                    	
				                 }else if ((temp1 - 5 >= 0 && temp2 - 5 >= 0) && PlayBoard.playBoard[temp1 - 3][temp2- 3] == 0 && PlayBoard.playBoard[temp1 - 4][temp2- 4] == Main.ComC && PlayBoard.playBoard[temp1 - 5][temp2- 5] == 0) {
				                	superWeight[temp1 - 5][temp2 - 5] += 40; add++;
			                    	superWeight[temp1 - 3][temp2 - 3] += 40; add++;
				                 }
				               temp1++;
				               temp2++;
			                  } else myCount = 0;
			             }
		            } catch (ArrayIndexOutOfBoundsException e) {}
		           }
		         
		      }
		   }
		   
		   
		   if(add >= 2) return;
		   // 3(좌대각\) 2공백 1 공격
		   for(int i = 0;i<19;i++){
		      myCount = 0;
		      for (int j = 0; j < 19; j++) {
		         int temp1 = i;
		         int temp2 = j;
		         for (int k = 0; k < 3; k++) {
		        	try {
			            if (PlayBoard.playBoard[temp1][temp2] == Main.ComC) {
			               myCount++;
			               if (myCount == 3) {
			                    if ((temp1 + 3 <= 18 && temp2 + 3 <= 18) && PlayBoard.playBoard[temp1 + 1][temp2+ 1] == 0 && PlayBoard.playBoard[temp1 + 2][temp2 + 2] == 0 && PlayBoard.playBoard[temp1 + 3][temp2+ 3] == Main.ComC) {
									superWeight[temp1 + 1][temp2+ 1] += 40; add++;
			                    	superWeight[temp1 + 2][temp2+ 2] += 40; add++;
			                    	
				                 }else if ((temp1 - 5 >= 0 && temp2 - 5 >= 0) && PlayBoard.playBoard[temp1 - 3][temp2- 3] == 0 && PlayBoard.playBoard[temp1 - 4][temp2- 4] == 0 && PlayBoard.playBoard[temp1 - 5][temp2- 5] == Main.ComC) {
				                	superWeight[temp1 - 4][temp2 - 4] += 40; add++;
			                    	superWeight[temp1 - 3][temp2 - 3] += 40; add++;
				                 }
				               temp1++;
				               temp2++;
			                  } else myCount = 0;
			            	}
		        	 }catch (ArrayIndexOutOfBoundsException e) {}
		         }
		      }
		   }
		   

		   if(add >= 2) return;
		      // 2 (공백2) 2 공격 (좌대각\)왼쪽 위에서 오른쪽 아래
		      for (int i = 0; i < 19; i++) {
		          myCount = 0;
		          for (int j = 0; j < 19; j++) {
		             int temp1 = i;
		              int temp2 = j;
		              for (int k = 0; k < 2; k++) {
		                 try {
		                      if (PlayBoard.playBoard[temp1][temp2] == Main.ComC) {
		                            myCount++;
		                            try {
		                               if (myCount == 2) {
		                            	   //101011(up)
		                                   if ((temp1 - 5 >= 0 && temp2 - 5 >= 0 )&& PlayBoard.playBoard[temp1 - 4][temp2 - 4] == 0 && PlayBoard.playBoard[temp1 - 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 - 3][temp2 - 3] == Main.ComC && PlayBoard.playBoard[temp1 - 5][temp2 - 5] == Main.ComC) {
		                                          superWeight[temp1 - 4][temp2 - 4] += 40; add++;
		                                          superWeight[temp1 - 2][temp2 - 2] += 40; add++;
		                                    }
		                                   //110011(up)
		                                    else if ((temp1 - 5 >= 0 && temp2 - 5 >= 0 )&& PlayBoard.playBoard[temp1 - 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 - 3][temp2 - 3] == 0 && PlayBoard.playBoard[temp1 - 4][temp2 - 4] == Main.ComC && PlayBoard.playBoard[temp1 - 5][temp2 - 5] == Main.ComC) {
		                                          superWeight[temp1 - 2][temp2 - 2] += 40; add++;
		                                          superWeight[temp1 - 3][temp2 - 3] += 40; add++;
		                                    }
		                                   //011011(up)
		                                    else if ((temp1 - 5 >= 0 && temp2 - 5 >= 0 )&& PlayBoard.playBoard[temp1 - 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 - 5][temp2 - 5] == 0 && PlayBoard.playBoard[temp1 - 4][temp2 - 4] == Main.ComC && PlayBoard.playBoard[temp1 - 3][temp2 - 3] == Main.ComC) {
		                                          superWeight[temp1 - 2][temp2 - 2] += 40; add++;
		                                          superWeight[temp1 - 5][temp2 - 5] += 40; add++;
		                                    }
		                                   //110110(mid)
		                                    else if ((temp1 - 4 >= 0 && temp2 - 4 >= 0 && temp1 + 1 <= 18 && temp2 + 1 <= 18) && PlayBoard.playBoard[temp1 + 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 - 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 - 3][temp2 - 3] == Main.ComC && PlayBoard.playBoard[temp1 - 4][temp2 - 4] == Main.ComC) {
		                                       superWeight[temp1 + 1][temp2 + 1] += 40; add++;
		                                       superWeight[temp1 - 2][temp2 - 2] += 40; add++;
		                                    }
		                                   //011011(mid)
		                                    else if ((temp1 - 2 >= 0 && temp2 - 2 >= 0 && temp1 + 3 <= 18 && temp2 + 3 <= 18) && PlayBoard.playBoard[temp1 - 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 + 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 + 2][temp2 + 2] == Main.ComC && PlayBoard.playBoard[temp1 + 3][temp2 + 3] == Main.ComC) {
		                                          superWeight[temp1 - 2][temp2 - 2] += 40; add++;
		                                          superWeight[temp1 + 1][temp2 + 1] += 40; add++;
		                                    }
		                                   //110110(mid)
		                                    else if ((temp1 - 4 >= 0 && temp2 - 4 >= 0 && temp1 + 1 <= 18 && temp2 + 1 <= 18) && PlayBoard.playBoard[temp1 + 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 - 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 - 3][temp2 - 3] == Main.ComC && PlayBoard.playBoard[temp1 - 4][temp2 - 4] == Main.ComC) {
		                                       superWeight[temp1 + 1][temp2 + 1] += 40; add++;
		                                       superWeight[temp1 - 2][temp2 - 2] += 40; add++;
		                                   }
		                                   //110110(down)
			                                   else if ((temp1- 4 <= 18 && temp2 - 4 <= 18) && PlayBoard.playBoard[temp1 + 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 + 4][temp2 + 4] == 0 && PlayBoard.playBoard[temp1 + 2][temp2 + 2] == Main.ComC && PlayBoard.playBoard[temp1 + 3][temp2 + 3] == Main.ComC) {
			                                       superWeight[temp1 + 1][temp2 + 1] += 40; add++;
			                                       superWeight[temp1 + 4][temp2 + 4] += 40; add++;
			                               }
		                                   //110011(down)
		                                    else if ((temp1- 4 <= 18 && temp2 - 4 <= 18) && PlayBoard.playBoard[temp1 + 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 + 2][temp2 + 2] == 0 && PlayBoard.playBoard[temp1 + 3][temp2 + 3] == Main.ComC && PlayBoard.playBoard[temp1 + 4][temp2 + 4] == Main.ComC) {
		                                          superWeight[temp1 + 1][temp2 + 1] += 40; add++;
		                                          superWeight[temp1 + 2][temp2 + 2] += 40; add++;
		                                   }
		                                   //110101(down)
		                                    else if ((temp1- 4 <= 18 && temp2 - 4 <= 18) && PlayBoard.playBoard[temp1 + 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 + 4][temp2 + 4] == 0 && PlayBoard.playBoard[temp1 + 3][temp2 + 3] == Main.ComC && PlayBoard.playBoard[temp1 + 2][temp2 + 2] == Main.ComC) {
		                                          superWeight[temp1 + 1][temp2 + 1] += 40; add++;
		                                          superWeight[temp1 + 4][temp2 + 4] += 40; add++;
		                                   }
		                               }
		                            } catch (ArrayIndexOutOfBoundsException e) {}
		                            temp1++;
		                            temp2++;
		                         } else myCount = 0;
		                 }
		                 catch(ArrayIndexOutOfBoundsException e) {}
		              }
		              }
		          }
		      
		      
		      //우대각 시작점 -----------------------------------------------------------------------------------------
		       
    if(add >= 2) return;
    // 5 오른쪽위에서 왼쪽아래 (우대각/) 공격 
	      for (int j = 0; j < 19; j++) {
	         myCount = 0;
	         for (int i = 0; i < 19; i++) {
	            int temp1 = i;
	            int temp2 = j;
	            for (int k = 0; k < 5; k++) {
	            	try {
	            	if (PlayBoard.playBoard[temp1][temp2] == Main.ComC) {
	            		myCount++;
	                     if (myCount == 5) {
	                    	 //우상단 막히고 좌하단 뚫림
	                    	if((temp1 + 5 > 18 || temp2 - 5 < 0) && (temp1 - 1 >= 0 || temp2 + 1 <= 18) && PlayBoard.playBoard[temp1 - 1][temp2 + 1] == 0) {
								superWeight[temp1 - 1][temp2 + 1] += 40; add++;
							}
	                    	//좌하단 막히고 우상단 뚫림
							else if((temp1 - 1 < 0 || temp2 + 1 > 18) && (temp1 + 5 <= 18 && temp2 - 5 >= 0) && PlayBoard.playBoard[temp1 + 5][temp2 - 5] == 0){
								superWeight[temp1 + 5][temp2 - 5] += 40; add++;
							}
	                    	//우상단 자리있음
							else if ((temp1 + 5 <= 18 && temp2 - 5 >= 0) && PlayBoard.playBoard[temp1 + 5][temp2 - 5] == 0) {
	                        	superWeight[temp1 + 5][temp2 - 5] += 80;  add++;
	                        } 
	                    	//좌하단 자리있음
							else if ((temp1 - 1 >= 0 || temp2 + 1 <= 18) && PlayBoard.playBoard[temp1 - 1][temp2 + 1] == 0) {
	                        	superWeight[temp1 - 1][temp2 + 1] += 80; add++;
	                        }
	                     }
	                     temp1--;
	                     temp2++;
	                } else myCount = 0;
	               } catch (ArrayIndexOutOfBoundsException e) {}
	            }
	         }
	      }
	      
	      
	      
	      if(add >= 2) return;
	      // 4 오른쪽위에서 왼쪽아래 (우대각/) 공격
	      for (int j = 0; j < 19; j++) {
	         myCount = 0;
	         for (int i = 0; i < 19; i++) {
	            int temp1 = i;
	            int temp2 = j;
	            for (int k = 0; k < 4; k++) {
	            	try {
		                if (PlayBoard.playBoard[temp1][temp2] == Main.ComC) {
		                  myCount++;
		                     if (myCount == 4) {
		                    	 //우상단 막히고 좌하단에 두개
		                    	 if((temp1 + 4 > 18 || temp2 - 4 < 0) && (temp1 - 2 >= 0 && temp2 + 2 <= 18) && PlayBoard.playBoard[temp1 - 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 - 2][temp2 + 2] == 0) {
									superWeight[temp1 - 1][temp2 + 1] += 40; add++;
									superWeight[temp1 - 2][temp2 + 2] += 40; add++;
								//촤하단 막히고 우상단에 두개
								}else if((temp1 + 1 > 18 || temp2 - 1 < 0) && (temp1 + 5 >= 0 && temp2 - 5 >= 0) && PlayBoard.playBoard[temp1 + 5][temp2 - 5] == 0 && PlayBoard.playBoard[temp1 + 4][temp2 - 4] == 0){
									superWeight[temp1 + 5][temp2 - 5] += 40; add++;
									superWeight[temp1 + 4][temp2 - 4] += 40; add++;
								//우상단 좌하단에 하나씩 뚫림 
								}else if ((temp1 + 4 <= 18 && temp2 - 4 >= 0) && (temp1 - 1 >= 0 && temp2 + 1 <= 18) && PlayBoard.playBoard[temp1 + 4][temp2 - 4] == 0 && PlayBoard.playBoard[temp1 - 1][temp2 + 1] == 0) {
		                        	superWeight[temp1 + 4][temp2 - 4] += 80; add++;
		                        	superWeight[temp1 - 1][temp2 + 1] += 80; add++;
		                        //우상단에 두개
		                        } else if ((temp1 + 5 >= 0 && temp2 - 5 >= 0) && PlayBoard.playBoard[temp1 + 5][temp2 - 5] == 0 && PlayBoard.playBoard[temp1 + 4][temp2 - 4] == 0) {
		                        	superWeight[temp1 + 5][temp2 - 5] += 40; add++;
									superWeight[temp1 + 4][temp2 - 4] += 40; add++;
	                        	//좌하단에 두개
	                           } else if ((temp1 - 2 >= 0 && temp2 +2 <= 18) && PlayBoard.playBoard[temp1 - 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 - 2][temp2 + 2] == 0) {
	                        	    superWeight[temp1 - 1][temp2 + 1] += 40; add++;
									superWeight[temp1 - 2][temp2 + 2] += 40; add++;
	                           }
		                     }
		                  temp1--;
		                  temp2++;
		               } else myCount = 0;
	               } catch (ArrayIndexOutOfBoundsException e) {}
	            }
	         }
	      }
	
		      
	      
	      
	      if(add >= 2) return;
	      // 3(우대각/) 1공백 공격
		   for(int j = 0; j < 19; j++){
		      myCount = 0;
		      for (int i = 0;i<19;i++) {
		         int temp1 = i;
		         int temp2 = j;
		         for (int k = 0; k < 3; k++) {
		        	 try {
			             if (PlayBoard.playBoard[temp1][temp2] == Main.ComC) {
			               myCount++;
			                  if (myCount == 3) {
			                    if ((temp1 - 3 >= 0 && temp2 + 3 <= 18) && PlayBoard.playBoard[temp1 - 1][temp2+ 1] == 0 && PlayBoard.playBoard[temp1 - 2][temp2 + 2] == Main.ComC && PlayBoard.playBoard[temp1 - 3][temp2+ 3] == 0) {
			                    	superWeight[temp1 - 1][temp2+ 1] += 40; add++;
			                    	superWeight[temp1 - 3][temp2+ 3] += 40; add++;
			                    	
				                 }else if ((temp1 + 5 <= 18 && temp2 - 5 >= 0) && PlayBoard.playBoard[temp1 + 3][temp2- 3] == 0 && PlayBoard.playBoard[temp1 + 4][temp2- 4] == Main.ComC && PlayBoard.playBoard[temp1 + 5][temp2- 5] == 0) {
				                	superWeight[temp1 + 5][temp2 - 5] += 40; add++;
			                    	superWeight[temp1 + 3][temp2 - 3] += 40; add++;
				                 }
				               temp1--;
				               temp2++;
			                  } else myCount = 0;
			             }
		            } catch (ArrayIndexOutOfBoundsException e) {}
		           }
		         
		      }
		   }
		   
		    
		   if(add >= 2) return;
		   // 3(우대각/) 2공백 1 공격
		   for(int j = 0; j < 19; j++){
		      myCount = 0;
		      for (int i = 0;i<19;i++) {
		         int temp1 = i;
		         int temp2 = j;
		         for (int k = 0; k < 3; k++) {
		        	try {
			            if (PlayBoard.playBoard[temp1][temp2] == Main.ComC) {
			               myCount++;
			               if (myCount == 3) {
			                    if ((temp1 - 3 >= 0 && temp2 + 3 <= 18) && PlayBoard.playBoard[temp1 - 1][temp2+ 1] == 0 && PlayBoard.playBoard[temp1 - 2][temp2 + 2] == 0 && PlayBoard.playBoard[temp1 - 3][temp2+ 3] == Main.ComC) {
									superWeight[temp1 - 1][temp2+ 1] += 40; add++;
			                    	superWeight[temp1 - 2][temp2+ 2] += 40; add++;
			                    	
				                 }else if ((temp1 + 5 <= 18 && temp2 - 5 >= 0) && PlayBoard.playBoard[temp1 + 3][temp2- 3] == 0 && PlayBoard.playBoard[temp1 + 4][temp2- 4] == 0 && PlayBoard.playBoard[temp1 + 5][temp2- 5] == Main.ComC) {
				                	superWeight[temp1 + 4][temp2 - 4] += 40; add++;
			                    	superWeight[temp1 + 3][temp2 - 3] += 40; add++;
				                 }
				               temp1--;
				               temp2++;
			                  } else myCount = 0;
			            	}
		        	 }catch (ArrayIndexOutOfBoundsException e) {}
		         }
		      }
		   }
		   
		   
		   
		 
		  
		   if(add >= 2) return;
		      // 2 (공백2) 2 공격 (우대각/)오른쪽 위에서 왼쪽 아래 
		      for (int i = 0; i < 19; i++) {
		          myCount = 0;
		          for (int j = 0; j < 19; j++) {
		              int temp1 = i;
		              int temp2 = j;
		              for (int k = 0; k < 2; k++) {
		                 try {
		                      if (PlayBoard.playBoard[temp1][temp2] == Main.ComC) {
		                            myCount++;
		                            try {
		                               if (myCount == 2) {
		                                   //011011(up)
		                                   if ((temp1 + 5 <= 18 && temp1 - 5 >= 0) && PlayBoard.playBoard[temp1 + 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 + 5][temp2 - 5] == 0 && PlayBoard.playBoard[temp1 + 3][temp2 - 3] == Main.ComC && PlayBoard.playBoard[temp1 + 4][temp2 - 4] == Main.ComC) {
		                                       superWeight[temp1 + 2][temp2 - 2] += 40; add++;
		                                       superWeight[temp1 + 5][temp2 - 5] += 40; add++;
		                                    }
		                                   //101011(up)
		                                    else if ((temp1 + 5 <= 18 && temp1 - 5 >= 0) && PlayBoard.playBoard[temp1 + 4][temp2 - 4] == 0 && PlayBoard.playBoard[temp1 + 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 + 3][temp2 - 3] == Main.ComC && PlayBoard.playBoard[temp1 + 5][temp2 - 5] == Main.ComC) {
		                                          superWeight[temp1 + 2][temp2 - 2] += 40; add++;
		                                          superWeight[temp1 + 4][temp2 - 4] += 40; add++;
		                                    }
		                                   //110011(up)
		                                    else if ((temp1 + 5 <= 18 && temp1 - 5 >= 0) && PlayBoard.playBoard[temp1 + 3][temp2 - 3] == 0 && PlayBoard.playBoard[temp1 + 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 + 4][temp2 - 4] == Main.ComC && PlayBoard.playBoard[temp1 + 5][temp2 - 5] == Main.ComC) {
		                                          superWeight[temp1 + 2][temp2 - 2] += 40; add++;
		                                          superWeight[temp1 + 3][temp2 - 3] += 40; add++;
		                                    }
		                                   //101101(mid)
		                                    else if ((temp1 - 2 >= 0 && temp2 + 2 <= 18) && (temp1 + 3 <= 18 && temp2 - 3 >= 0) && PlayBoard.playBoard[temp1 + 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 - 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 - 2][temp2 + 2] == Main.ComC && PlayBoard.playBoard[temp1 + 3][temp2 - 3] == Main.ComC) {
		                                          superWeight[temp1 + 2][temp2 - 2] += 40; add++;
		                                          superWeight[temp1 - 1][temp2 + 1] += 40; add++;
		                                    }
		                                   //011011(mid)
			                            	   else if ((temp1 - 3 >= 0 && temp2 + 3 <= 18) && (temp1 + 2 <= 18 && temp2 - 2 >= 0) && PlayBoard.playBoard[temp1 - 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 + 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 - 2][temp2 + 2] == Main.ComC && PlayBoard.playBoard[temp1 - 3][temp2 + 3] == Main.ComC) {
			                                       superWeight[temp1 - 1][temp2 + 1] += 40; add++;
			                                       superWeight[temp1 + 2][temp2 - 2] += 40; add++;
			                               } 
		                                   //110110(mid)
			                            	   else if ((temp1 - 4 >= 0 && temp2 + 4 <= 18) && (temp1 + 1 <= 18 && temp2 - 1 >= 0) && PlayBoard.playBoard[temp1 - 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 + 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 + 3][temp2 - 3] == Main.ComC && PlayBoard.playBoard[temp1 + 4][temp2 - 4] == Main.ComC) {
			                                       superWeight[temp1 - 1][temp2 + 1] += 40; add++;
			                                       superWeight[temp1 + 2][temp2 - 2] += 40; add++;
			                               }
		                                   //110011(down)
		                                    else if ((temp1 - 4 >= 0 && temp1 +4 <= 18) && PlayBoard.playBoard[temp1 - 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 - 2][temp2 + 2] == 0 && PlayBoard.playBoard[temp1 - 3][temp2 + 3] == Main.ComC && PlayBoard.playBoard[temp1 - 4][temp2 + 4] == Main.ComC) {
		                                          superWeight[temp1 - 1][temp2 + 1] += 40; add++;
		                                          superWeight[temp1 - 2][temp2 + 2] += 40; add++;
		                                    }
		                                   //110101(down)
		                                    else if ((temp1 - 4 >= 0 && temp1 +4 <= 18) && PlayBoard.playBoard[temp1 - 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 - 3][temp2 + 3] == 0 && PlayBoard.playBoard[temp1 - 2][temp2 + 2] == Main.ComC && PlayBoard.playBoard[temp1 - 4][temp2 + 4] == Main.ComC) {
		                                          superWeight[temp1 - 1][temp2 + 1] += 40; add++;
		                                          superWeight[temp1 - 3][temp2 + 3] += 40; add++;
		                                    }
		                                   //110110(down)
		                                    else if ((temp1 - 4 >= 0 && temp1 +4 <= 18) && PlayBoard.playBoard[temp1 - 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 - 4][temp2 + 4] == 0 && PlayBoard.playBoard[temp1 - 3][temp2 + 3] == Main.ComC && PlayBoard.playBoard[temp1 - 2][temp2 + 2] == Main.ComC) {
		                                          superWeight[temp1 - 1][temp2 + 1] += 40; add++;
		                                          superWeight[temp1 - 4][temp2 + 4] += 40; add++;
		                                    }
		                               }
		                            } catch (ArrayIndexOutOfBoundsException e) {}
		                            temp1--;
		                            temp2++;
		                         } else myCount = 0;
		                 }
		                 catch(ArrayIndexOutOfBoundsException e) {}
		              }
		              }
		          }
		    
		    //작업중입니다 ==========================================
				
		    //====================================================================
	   
		      
		    testTest(myCount, add);

		   
		   
   

	      //// 안놓으면 질 때, 한방방어 
	      //// ----------------------------------------------------------------------------------
	      
	      if(add >= 2) return;
	      // 5 세로 방어
	      for (int i = 0; i < 19; i++) {
	         myCount = 0;
	         for (int j = 0; j < 19; j++) {
	            try {
	               if (PlayBoard.playBoard[i][j] == Main.UserC) {
	                  myCount++;
	                  if (myCount == 5) {
	                     // 양쪽 다 비었으면 양쪽 시급하게 막고
	                		if (j - 5 < 0 && PlayBoard.playBoard[i][j + 1] == 0) {
								superWeight[i][j + 1] += 40;
								add++;
							}

							else if (j + 1 > 18 && PlayBoard.playBoard[i][j - 5] == 0) {
								superWeight[i][j - 5] += 40;
								add++;
							}
							else if (PlayBoard.playBoard[i][j - 5] == 0 && PlayBoard.playBoard[i][j + 1] == 0) {
	                    	 superWeight[i][j - 5] += 40; add++;
	                    	 superWeight[i][j + 1] += 40; add++;
	                     }
	                     // 둘중 한쪽만 비었으면 거기 막기
	                     else if (PlayBoard.playBoard[i][j - 5] == 0) {
	                    	 superWeight[i][j - 5] += 40; add++;
	                     } else if (PlayBoard.playBoard[i][j + 1] == 0) {
	                    	 superWeight[i][j + 1] += 40; add++;
	                     }
	                  }
	               } else myCount = 0;
	            } catch (ArrayIndexOutOfBoundsException e) {}
	         }
	      }
	      
	      if(add >= 2) return;
	      // 4 세로 방어
	      for (int i = 0; i < 19; i++) {
	         myCount = 0;
	         for (int j = 0; j < 19; j++) {
	            try {
	               if (PlayBoard.playBoard[i][j] == Main.UserC) {
	                  myCount++;
	                  if (myCount == 4) { // 양끝 뚫려있으면 양끝에 가중치
	                	  if (j - 4 < 0 && PlayBoard.playBoard[i][j + 1] == 0) {
								superWeight[i][j + 1] += 40;
								add++;
							}

							else if (j + 1 > 18 && PlayBoard.playBoard[i][j - 4] == 0) {
								superWeight[i][j - 4] += 40;
								add++;
							}
							else if (PlayBoard.playBoard[i][j - 4] == 0 && PlayBoard.playBoard[i][j + 1] == 0) {
	                    	 superWeight[i][j - 4] += 40; add++;
	                    	 superWeight[i][j + 1] += 40; add++;
	                     } // 한쪽만 뚫려있으면
	                     else if (PlayBoard.playBoard[i][j - 4] == 0) {
	                        if (PlayBoard.playBoard[i][j - 5] == 0) { //한쪽이 더 뚫려있으면 둘중 이득인곳에 두고 
	                        	if(weight[i][j - 4]>weight[i][j - 5] ) {
	                        		superWeight[i][j - 4] += 40; add++;
	                        	}else{
	                        		superWeight[i][j - 5] += 40; add++;
	                        	}
	                        }else { //아니면 거따두고 
	                        	superWeight[i][j - 4] += 40; add++;
	                        }
	                     }else if(PlayBoard.playBoard[i][j + 1] == 0) {
	                    	 if (PlayBoard.playBoard[i][j + 2] == 0) {
		                        	if(weight[i][j + 2]>weight[i][j + 1]) {
		                        		superWeight[i][j + 2] += 40; add++;
		                        	}else{
		                        		superWeight[i][j + 1] += 40; add++;
		                        	}
		                      }else {
		                    	  superWeight[i][j + 1] += 40; add++;
		                      }
	                     }
	                  }
	               } else myCount = 0;
	            } catch (ArrayIndexOutOfBoundsException e) {}
	         }
	      }
	      
	      
	      if(add >= 2) return;
	      // 3세로 1 공백 방어
	      for (int i = 0; i < 19; i++) {
	         myCount = 0;
	         for (int j = 0; j < 19; j++) {
	            try {
	               if (PlayBoard.playBoard[i][j] == Main.UserC) {
	                  myCount++;
	                  if (myCount == 3) { //세번연속일때 
	                	  //111010 
	                     if (PlayBoard.playBoard[i][j + 1] == 0 && PlayBoard.playBoard[i][j + 2] == Main.UserC && PlayBoard.playBoard[i][j + 3] == 0) {
	                        if(weight[i ][j+ 3] > weight[i ][j+ 1]) {
	                        	superWeight[i][j + 3] += 40; add++;
	                        }else {
	                           weight[i][j + 1] += 40; add++;
	                        }
	                     } 
	                     //010111
	                     else if (PlayBoard.playBoard[i ][j- 3] == 0 && PlayBoard.playBoard[i ][j- 4] == Main.UserC && PlayBoard.playBoard[i ][j- 5] == 0) {
	                        if(weight[i ][j- 3]  > weight[i ][j- 5]) {
	                        	superWeight[i ][j- 3] += 40; add++;}
	                        else {
	                        	superWeight[i ][j- 5] += 40; add++;}
	                        }
	                  }
	               } else myCount = 0;
	            } catch (ArrayIndexOutOfBoundsException e) {}
	         }
	      }
	      
	         if(add >= 2) return;
	         // 3세로 2 공백 방어
	         for (int i = 0; i < 19; i++) {
	            myCount = 0;
	            for (int j = 0; j < 19; j++) {
	               try {
	                  if (PlayBoard.playBoard[i][j] == Main.UserC) {
	                     myCount++;
	                     if (myCount == 3) { //세번연속일때 
	                        //111001
	                        if (PlayBoard.playBoard[i][j + 1] == 0 && PlayBoard.playBoard[i][j + 2] == 0 && PlayBoard.playBoard[i][j + 3] == Main.UserC) {
	                           if(weight[i ][j+ 2] > weight[i ][j+ 1]) {
	                              superWeight[i][j + 2] += 40; add++;
	                           }else {
	                        	   superWeight[i][j + 1] += 40; add++;
	                           }
	                        } 
	                        //100111
	                        else if (PlayBoard.playBoard[i ][j- 3] == 0 && PlayBoard.playBoard[i ][j- 4] == 0 && PlayBoard.playBoard[i ][j- 5] == Main.UserC) {
	                           if(weight[i ][j- 4]  > weight[i ][j- 3]) {
	                              superWeight[i ][j- 4] += 40; add++;}
	                           else {
	                              superWeight[i ][j- 3] += 40; add++;}
	                           }
	                     }
	                  } else myCount = 0;
	               } catch (ArrayIndexOutOfBoundsException e) {}
	            }
	         }
	      
	      if(add >= 2) return;
	         // 2 (공백2) 2 방어 세로
	         for (int i = 0; i < 19; i++) {
	             myCount = 0;
	             for (int j = 0; j < 19; j++) {
	                try {
	                   if (PlayBoard.playBoard[i][j] == Main.UserC) {
	                      myCount++;
	                      if (myCount == 2) {
	                         if (PlayBoard.playBoard[i][j + 1] == 0 && PlayBoard.playBoard[i][j + 2] == 0 && PlayBoard.playBoard[i][j + 3] == Main.UserC && PlayBoard.playBoard[i][j + 4] == Main.UserC) {
	                            superWeight[i][j + 1] += 40; add++;
	                            superWeight[i][j + 2] += 40; add++;
	                
	                         } 
	                         else if (PlayBoard.playBoard[i][j - 2] == 0 && PlayBoard.playBoard[i][j - 3] == 0 && PlayBoard.playBoard[i][j - 4] == Main.UserC && PlayBoard.playBoard[i][j - 5] == Main.UserC) {
	                            superWeight[i][j - 2] += 40; add++;
	                            superWeight[i][j - 3] += 40; add++;
	                         }
	                         else if(PlayBoard.playBoard[i][j - 2] == 0 && PlayBoard.playBoard[i][j + 1] == 0 && PlayBoard.playBoard[i][j + 2] == Main.UserC && PlayBoard.playBoard[i][j + 3] == Main.UserC) {
	                            superWeight[i][j - 2] += 40; add++;
	                            superWeight[i][j + 1] += 40; add++;
	                         }
	                         else if(PlayBoard.playBoard[i][j - 2] == 0 && PlayBoard.playBoard[i][j - 5] == 0 && PlayBoard.playBoard[i][j - 3] == Main.UserC && PlayBoard.playBoard[i][j - 4] == Main.UserC) {
	                            superWeight[i][j - 2] += 40; add++;
	                            superWeight[i][j - 5] += 40; add++;
	                         }
	                         else if(PlayBoard.playBoard[i][j - 2] == 0 && PlayBoard.playBoard[i][j - 4] == 0 && PlayBoard.playBoard[i][j - 3] == Main.UserC && PlayBoard.playBoard[i][j - 5] == Main.UserC) {
	                            superWeight[i][j - 2] += 40; add++;
	                            superWeight[i][j - 4] += 40; add++;
	                         }
	                         else if(PlayBoard.playBoard[i][j - 2] == 0 && PlayBoard.playBoard[i][j + 1] == 0 && PlayBoard.playBoard[i][j - 3] == Main.UserC && PlayBoard.playBoard[i][j + 2] == Main.UserC) {
	                            superWeight[i][j - 2] += 40; add++;
	                            superWeight[i][j + 1] += 40; add++;
	                         }
	                         else if(PlayBoard.playBoard[i][j + 1] == 0 && PlayBoard.playBoard[i][j + 3] == 0 && PlayBoard.playBoard[i][j + 2] == Main.UserC && PlayBoard.playBoard[i][j + 4] == Main.UserC) {
	                            superWeight[i][j + 3] += 40; add++;
	                            superWeight[i][j + 1] += 40; add++;
	                         }
	                         else if(PlayBoard.playBoard[i][j + 1] == 0 && PlayBoard.playBoard[i][j + 4] == 0 && PlayBoard.playBoard[i][j + 2] == Main.UserC && PlayBoard.playBoard[i][j + 3] == Main.UserC) {
	                            superWeight[i][j + 4] += 40; add++;
	                            superWeight[i][j + 1] += 40; add++;
	                         }
	                         else if(PlayBoard.playBoard[i][j + 1] == 0 && PlayBoard.playBoard[i][j - 2] == 0 && PlayBoard.playBoard[i][j - 3] == Main.UserC && PlayBoard.playBoard[i][j - 4] == Main.UserC) {
	                            superWeight[i][j - 2] += 40; add++;
	                            superWeight[i][j + 1] += 40; add++;
	                         }
	                      } 
	                   }else myCount = 0;
	                } catch (ArrayIndexOutOfBoundsException e) {}
	             }
	          }
	      
	      

	      if(add >= 2) return;
	      // 5 가로 방어
	      for (int j = 0; j < 19; j++) {
	         myCount = 0;
	         for (int i = 0; i < 19; i++) {
	            try {
	               if (PlayBoard.playBoard[i][j] == Main.UserC) {
	                  myCount++;
	                  if (myCount == 5) {
	                     // 양쪽 다 비었으면 양쪽 시급하게 막고
	                	  if (i - 5 < 0 && PlayBoard.playBoard[i + 1][j] == 0) {
								superWeight[i + 1][j] += 40;
								add++;
							}

							else if (i + 1 > 18 && PlayBoard.playBoard[i - 5][j] == 0) {
								superWeight[i - 5][j] += 40;
								add++;
							}
							else if (PlayBoard.playBoard[i - 5][j] == 0 && PlayBoard.playBoard[i + 1][j] == 0) {
	                    	 superWeight[i - 5][j] += 40; add++;
	                    	 superWeight[i + 1][j] += 40; add++;
	                     }
	                     // 둘중 한쪽만 비었으면 거기 막기
	                     else if (PlayBoard.playBoard[i - 5][j] == 0) {
	                    	 superWeight[i - 5][j] += 40; add++;
	                     } else if (PlayBoard.playBoard[i + 1][j] == 0) {
	                    	 superWeight[i + 1][j] += 40; add++;
	                     }
	                  }
	               } else myCount = 0;
	            } catch (ArrayIndexOutOfBoundsException e) {}
	         }
	      }

	      
	      if(add >= 2) return;
	      // 4 가로 방어
	      for (int j = 0; j < 19; j++) {
	         myCount = 0;
	         for (int i = 0; i < 19; i++) {
	            try {
	               if (PlayBoard.playBoard[i][j] == Main.UserC) {
	                  myCount++;
	                  if (myCount == 4) { // 양끝 뚫려있으면 양끝에 가중치
	                	  if (i - 4 < 0 && PlayBoard.playBoard[i + 1][j] == 0) {
								superWeight[i + 1][j] += 40;
								add++;
							}

							else if (i + 1 > 18 && PlayBoard.playBoard[i - 4][j] == 0) {
								superWeight[i - 4][j] += 40;
								add++;
							}
							else if (PlayBoard.playBoard[i - 4][j] == 0 && PlayBoard.playBoard[i + 1][j] == 0) {
	                    	 superWeight[i - 4][j] += 40; add++;
	                    	 superWeight[i + 1][j] += 40; add++;
	                     } // 한쪽만 뚫려있으면
	                     else if (PlayBoard.playBoard[i - 4][j] == 0) {
	                        if (PlayBoard.playBoard[i - 5][j] == 0) { //한쪽이 더 뚫려있으면 둘중 이득인곳에 두고 
	                        	if(weight[i - 4][j]>weight[i - 5][j] ) {
	                        		superWeight[i - 4][j] += 40; add++;
	                        	}else{
	                        		superWeight[i - 5][j] += 40; add++;
	                        	}
	                        }else { //아니면 거따두고 
	                        	superWeight[i - 4][j] += 40; add++;
	                        }
	                     }else if(PlayBoard.playBoard[i + 1][j] == 0) {
	                    	 if (PlayBoard.playBoard[i + 2][j] == 0) {
		                        	if(weight[i + 2][j]>weight[i + 1][j]) {
		                        		superWeight[i + 2][j] += 40; add++;
		                        	}else{
		                        		superWeight[i+ 1][j ] += 40; add++;
		                        	}
		                      }else {
		                    	  superWeight[i + 1][j] += 40; add++;
		                      }
	                     }
	                  }
	               } else myCount = 0;
	            } catch (ArrayIndexOutOfBoundsException e) {}
	         }
	      }
	      
	      
	      if(add >= 2) return;
	      // 3가로 1 공백 방어 
	      for (int j = 0; j < 19; j++) {
	         myCount = 0;
	         for (int i = 0; i < 19; i++) {
	            try {
	               if (PlayBoard.playBoard[i][j] == Main.UserC) {
	                  myCount++;
	                  if (myCount == 3) {
	                     if (PlayBoard.playBoard[i + 1][j] == 0 && PlayBoard.playBoard[i + 2][j] == Main.UserC && PlayBoard.playBoard[i + 3][j] == 0) {
	                        if(weight[i + 3][j] > weight[i + 1][j]) {
	                        	superWeight[i + 3][j] += 40; add++;
	                        }else {
	                           weight[i + 1][j] += 40; add++;
	                        }
	                     } 
	                     else if (PlayBoard.playBoard[i - 3][j] == 0 && PlayBoard.playBoard[i - 4][j] == Main.UserC && PlayBoard.playBoard[i - 5][j] == 0) {
	                        if(weight[i - 3][j]  > weight[i - 5][j]) {
	                        	superWeight[i - 3][j] += 40; add++;}
	                        else {
	                        	superWeight[i - 5][j] += 40; add++;}
	                        }
	                  }
	               } else myCount = 0;
	            } catch (ArrayIndexOutOfBoundsException e) {}
	         }
	      }
	      

	         if(add >= 2) return;
	         // 3가로 2 공백 방어 
	         for (int j = 0; j < 19; j++) {
	            myCount = 0;
	            for (int i = 0; i < 19; i++) {
	               try {
	                  if (PlayBoard.playBoard[i][j] == Main.UserC) {
	                     myCount++;
	                     if (myCount == 3) {
	                        if (PlayBoard.playBoard[i + 1][j] == 0 && PlayBoard.playBoard[i + 2][j] == 0 && PlayBoard.playBoard[i + 3][j] == Main.UserC) {
	                           if(weight[i + 2][j] > weight[i + 1][j]) {
	                              superWeight[i + 2][j] += 40; add++;
	                           }else {
	                        	   superWeight[i + 1][j] += 40; add++;
	                           }
	                        } 
	                        else if (PlayBoard.playBoard[i - 3][j] == 0 && PlayBoard.playBoard[i - 4][j] == 0 && PlayBoard.playBoard[i - 5][j] == Main.UserC ) {
	                           if(weight[i - 3][j]  > weight[i - 4][j]) {
	                              superWeight[i - 3][j] += 40; add++;}
	                           else {
	                              superWeight[i - 4][j] += 40; add++;}
	                           }
	                     }
	                  } else myCount = 0;
	               } catch (ArrayIndexOutOfBoundsException e) {}
	            }
	         }
	      
	      if(add >= 2) return;
	         // 2 (공백2) 2 방어 가로
	         for (int j = 0; j < 19; j++) {
	             myCount = 0;
	             for (int i = 0; i < 19; i++) {
	                try {
	                   if (PlayBoard.playBoard[i][j] == Main.UserC) {
	                      myCount++;
	                      if (myCount == 2) {
	                         if (PlayBoard.playBoard[i + 1][j] == 0 && PlayBoard.playBoard[i + 2][j] == 0 && PlayBoard.playBoard[i + 3][j] == Main.UserC && PlayBoard.playBoard[i + 4][j] == Main.UserC) {
	                            superWeight[i + 1][j] += 40; add++;
	                            superWeight[i + 2][j] += 40; add++;
	                
	                         } 
	                         else if (PlayBoard.playBoard[i - 2][j] == 0 && PlayBoard.playBoard[i - 3][j] == 0 && PlayBoard.playBoard[i - 4][j] == Main.UserC && PlayBoard.playBoard[i - 5][j] == Main.UserC) {
	                            superWeight[i - 2][j] += 40; add++;
	                            superWeight[i - 3][j] += 40; add++;
	                         }
	                         else if(PlayBoard.playBoard[i - 2][j] == 0 && PlayBoard.playBoard[i + 1][j] == 0 && PlayBoard.playBoard[i + 2][j] == Main.UserC && PlayBoard.playBoard[i + 3][j] == Main.UserC) {
	                            superWeight[i - 2][j] += 40; add++;
	                            superWeight[i + 1][j] += 40; add++;
	                         }
	                         else if(PlayBoard.playBoard[i - 2][j] == 0 && PlayBoard.playBoard[i - 5][j] == 0 && PlayBoard.playBoard[i - 3][j] == Main.UserC && PlayBoard.playBoard[i - 4][j] == Main.UserC) {
	                            superWeight[i - 2][j] += 40; add++;
	                            superWeight[i - 5][j] += 40; add++;
	                         }
	                         else if(PlayBoard.playBoard[i - 2][j] == 0 && PlayBoard.playBoard[i - 4][j] == 0 && PlayBoard.playBoard[i - 3][j] == Main.UserC && PlayBoard.playBoard[i - 5][j] == Main.UserC) {
	                            superWeight[i - 2][j] += 40; add++;
	                            superWeight[i - 4][j] += 40; add++;
	                         }
	                         else if(PlayBoard.playBoard[i - 2][j] == 0 && PlayBoard.playBoard[i + 1][j] == 0 && PlayBoard.playBoard[i - 3][j] == Main.UserC && PlayBoard.playBoard[i + 2][j] == Main.UserC) {
	                            superWeight[i - 2][j] += 40; add++;
	                            superWeight[i + 1][j] += 40; add++;
	                         }
	                         else if(PlayBoard.playBoard[i + 1][j] == 0 && PlayBoard.playBoard[i + 3][j] == 0 && PlayBoard.playBoard[i + 2][j] == Main.UserC && PlayBoard.playBoard[i + 4][j] == Main.UserC) {
	                            superWeight[i + 3][j] += 40; add++;
	                            superWeight[i + 1][j] += 40; add++;
	                         }
	                         else if(PlayBoard.playBoard[i + 1][j] == 0 && PlayBoard.playBoard[i + 4][j] == 0 && PlayBoard.playBoard[i + 2][j] == Main.UserC && PlayBoard.playBoard[i + 3][j] == Main.UserC) {
	                            superWeight[i + 4][j] += 40; add++;
	                            superWeight[i + 1][j] += 40; add++;
	                         }
	                         else if(PlayBoard.playBoard[i + 1][j] == 0 && PlayBoard.playBoard[i - 2][j] == 0 && PlayBoard.playBoard[i - 3][j] == Main.UserC && PlayBoard.playBoard[i - 4][j] == Main.UserC) {
	                            superWeight[i - 2][j] += 40; add++;
	                            superWeight[i + 1][j] += 40; add++;
	                         }
	                      } 
	                   }else myCount = 0;
	                } catch (ArrayIndexOutOfBoundsException e) {}
	             }
	          }
	      
	      
	      
	      
	      if(add >= 2) return;
	  	// 5 왼쪽 위에서 오른쪽 아래(좌대각\) 방어
	  	   for(int i = 0;i<19;i++){
	  	      myCount = 0;
	  	      for (int j = 0; j < 19; j++) {
	  	         int temp1 = i;
	  	         int temp2 = j;
	  	         for (int k = 0; k < 5; k++) {
	  	        	try {
	  	            if (PlayBoard.playBoard[temp1][temp2] == Main.UserC) {
	  	               myCount++;
	  	                  if (myCount == 5) {
	  	                	  //둘다뚫림 
	  	                	if((temp1 - 5 < 0 || temp2 - 5 < 0) && PlayBoard.playBoard[temp1 + 1][temp2 + 1] == 0) {
								superWeight[temp1 + 1][temp2 + 1] += 40;
								add++;
							}
							else if((temp1 + 1 > 18 || temp2 + 1 > 18) && PlayBoard.playBoard[temp1 - 5][temp2 - 5] == 0){
								superWeight[temp1 - 5][temp2 - 5] += 40;
								add++;
							}
							else if (PlayBoard.playBoard[temp1 - 5][temp2 - 5] == 0
	  	                           && PlayBoard.playBoard[temp1 + 1][temp2 + 1] == 0) {
	  	                    	superWeight[temp1 - 5][temp2 - 5] += 40; add++;
	  	                    	superWeight[temp1 + 1][temp2 + 1] += 40; add++;
	  	                     } 
	  	                     //한쪽만뚫림 - 그 뚫린곳에 가중치 
	  	                     else if (PlayBoard.playBoard[temp1 - 5][temp2 - 5] == 0
	  	                           || PlayBoard.playBoard[temp1 + 1][temp2 + 1] == 0) {
	  	                        if (PlayBoard.playBoard[temp1 - 5][temp2 - 5] == 0) {
	  	                        	superWeight[temp1 - 5][temp2 - 5] += 40; add++;
	  	                        } else if (PlayBoard.playBoard[temp1 + 1][temp2 + 1] == 0) {
	  	                        	superWeight[temp1 + 1][temp2 + 1] += 40; add++;
	  	                        }
	  	                     }
	  	                  }
		  	               temp1++;
		  	               temp2++;
	  	               } else myCount = 0;
	  	            } catch (ArrayIndexOutOfBoundsException e) {}
	  	         }
	  	      }
	  	   }
	  	   
	  	   

	      if(add >= 2) return;
	   // 4 왼쪽 위에서 오른쪽 아래(좌대각\) 방어
	   for(int i = 0;i<19;i++){
	      myCount = 0;
	      for (int j = 0; j < 19; j++) {
	         int temp1 = i;
	         int temp2 = j;
	         for (int k = 0; k < 4; k++) {
	        	 try {
	            if (PlayBoard.playBoard[temp1][temp2] == Main.UserC) {
	               myCount++;
	                  if (myCount == 4) {
	                	  if((temp1 - 4 < 0 || temp2 - 4 < 0) && PlayBoard.playBoard[temp1 + 1][temp2 + 1] == 0) {
								superWeight[temp1 + 1][temp2 + 1] += 40;
								add++;
							}
							else if((temp1 + 1 > 18 || temp2 + 1 > 18) && PlayBoard.playBoard[temp1 - 4][temp2 - 4] == 0){
								superWeight[temp1 - 4][temp2 - 4] += 40;
								add++;
							}
							else if (PlayBoard.playBoard[temp1 - 4][temp2 - 4] == 0
	                           && PlayBoard.playBoard[temp1 + 1][temp2 + 1] == 0) {
	                    	 superWeight[temp1 - 4][temp2 - 4] += 40; add++;
	                    	 superWeight[temp1 + 1][temp2 + 1] += 40; add++;
	                     } else if (PlayBoard.playBoard[temp1 - 4][temp2 - 4] == 0
	                           || PlayBoard.playBoard[temp1 + 1][temp2 + 1] == 0) {
	                        if (PlayBoard.playBoard[temp1 - 4][temp2 - 4] == 0
	                              && PlayBoard.playBoard[temp1 - 5][temp2 - 5] == 0) {
	                        	if(weight[temp1 - 4][temp2 - 4]>weight[temp1 - 5][temp2 - 5]) {
	                        		superWeight[temp1 - 4][temp2 - 4] += 40; add++;
	                        	}else{
	                        		superWeight[temp1 - 5][temp2 - 5] += 40; add++;
	                        	}
	                        } else if (PlayBoard.playBoard[temp1 + 1][temp2 + 1] == 0
	                              && PlayBoard.playBoard[temp1 + 2][temp2 + 2] == 0) {
	                        	if(weight[temp1 + 1][temp2 + 1]>weight[temp1 + 2][temp2 + 2]) {
	                        		superWeight[temp1 + 1][temp2 + 1] += 40; add++;
	                        	}else{
	                        		superWeight[temp1 + 2][temp2 + 2] += 40; add++;
	                        	}
	                        }
	                     }
	                  }
			              temp1++;
			              temp2++;
		              } else myCount = 0;
	            } catch (ArrayIndexOutOfBoundsException e) {}
	         }
	      }
	   }
	   

	   if(add >= 2) return;
	   // 3(좌대각\) 1공백 방어
	   for(int i = 0;i<19;i++){
	      myCount = 0;
	      for (int j = 0; j < 19; j++) {
	         int temp1 = i;
	         int temp2 = j;
	         for (int k = 0; k < 3; k++) {
	        	 try {
	            if (PlayBoard.playBoard[temp1][temp2] == Main.UserC) {
	               myCount++;
	                  if (myCount == 3) {
	                    if (PlayBoard.playBoard[temp1 + 1][temp2+ 1] == 0 && PlayBoard.playBoard[temp1 + 2][temp2 + 2] == Main.UserC && PlayBoard.playBoard[temp1 + 3][temp2+ 3] == 0) {
		                    if(weight[temp1 + 3][temp2+ 3] > weight[temp1 + 1][temp2+ 1]) {
		                    	superWeight[temp1 + 3][temp2+ 3] += 40; add++;
		                    }else {
		                       weight[temp1 + 1][temp2+ 1] += 40; add++;
		                    }
		                 } 
		                 else if (PlayBoard.playBoard[temp1 - 3][temp2- 3] == 0 && PlayBoard.playBoard[temp1 - 4][temp2- 4] == Main.UserC && PlayBoard.playBoard[temp1 - 5][temp2- 5] == 0) {
		                    if(weight[temp1 - 3][temp2- 3]  > weight[temp1 - 5][temp2- 5]) {
		                    	superWeight[temp1- 3][temp2- 3] += 40; add++;}
		                    else {
		                    	superWeight[temp1 - 5][temp2- 5] += 40; add++;}
		                    }
	                  }
		                  temp1++;
		                  temp2++;
	                  } else myCount = 0;
	        	 } catch (ArrayIndexOutOfBoundsException e) {}
	         }
	      }
	   }
	   
	   if(add >= 2) return;
	   // 3(좌대각\) 2공백 1 방어
	   for(int i = 0;i<19;i++){
	      myCount = 0;
	      for (int j = 0; j < 19; j++) {
	         int temp1 = i;
	         int temp2 = j;
	         for (int k = 0; k < 3; k++) {
	        	 try {
	            if (PlayBoard.playBoard[temp1][temp2] == Main.UserC) {
	               myCount++;
	                  if (myCount == 3) {
	                    if (PlayBoard.playBoard[temp1 + 1][temp2+ 1] == 0 && PlayBoard.playBoard[temp1 + 2][temp2 + 2] == 0 && PlayBoard.playBoard[temp1 + 3][temp2+ 3] == Main.UserC) {
		                    if(weight[temp1 + 1][temp2+ 1] > weight[temp1 + 2][temp2+ 2]) {
		                    	superWeight[temp1 + 1][temp2+ 1] += 40; add++;
		                    }else {
		                       weight[temp1 + 2][temp2+ 2] += 40; add++;
		                    }
		                 } 
		                 else if (PlayBoard.playBoard[temp1 - 3][temp2- 3] == 0 && PlayBoard.playBoard[temp1 - 4][temp2- 4] == 0 && PlayBoard.playBoard[temp1 - 5][temp2- 5] == Main.UserC) {
		                    if(weight[temp1 - 3][temp2- 3]  > weight[temp1 - 4][temp2- 4]) {
		                    	superWeight[temp1- 3][temp2- 3] += 40; add++;}
		                    else {
		                    	superWeight[temp1 - 4][temp2- 4] += 40; add++;}
		                    }
	                  }
		                  temp1++;
		                  temp2++;
	                  } else myCount = 0;
	        	 } catch (ArrayIndexOutOfBoundsException e) {}
	         }
	      }
	   }
	   
	   
	   if(add >= 2) return;
	      // 2 (공백2) 2 방어 (좌대각\)왼쪽 위에서 오른쪽 아래
	      for (int i = 0; i < 19; i++) {
	          myCount = 0;
	          for (int j = 0; j < 19; j++) {
	             int temp1 = i;
	              int temp2 = j;
	              for (int k = 0; k < 2; k++) {
	                 try {
	                      if (PlayBoard.playBoard[temp1][temp2] == Main.UserC) {
	                            myCount++;
	                            try {
	                               if (myCount == 2) {
	                                   if (PlayBoard.playBoard[temp1 + 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 + 4][temp2 + 4] == 0 && PlayBoard.playBoard[temp1 + 2][temp2 + 2] == Main.UserC && PlayBoard.playBoard[temp1 + 3][temp2 + 3] == Main.UserC) {
	                                       superWeight[temp1 + 1][temp2 + 1] += 40; add++;
	                                       superWeight[temp1 + 4][temp2 + 4] += 40; add++;
	                           
	                                    } 
	                                    else if (PlayBoard.playBoard[temp1 + 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 - 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 - 3][temp2 - 3] == Main.UserC && PlayBoard.playBoard[temp1 - 4][temp2 - 4] == Main.UserC) {
	                                       superWeight[temp1 + 1][temp2 + 1] += 40; add++;
	                                       superWeight[temp1 - 2][temp2 - 2] += 40; add++;
	                                    }
	                                    else if (PlayBoard.playBoard[temp1 + 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 + 2][temp2 + 2] == 0 && PlayBoard.playBoard[temp1 + 2][temp2 + 2] == Main.UserC && PlayBoard.playBoard[temp1 + 4][temp2 + 4] == Main.UserC) {
	                                          superWeight[temp1 + 1][temp2 + 1] += 40; add++;
	                                          superWeight[temp1 + 2][temp2 + 2] += 40; add++;
	                                    }
	                                    else if (PlayBoard.playBoard[temp1 + 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 + 2][temp2 + 2] == 0 && PlayBoard.playBoard[temp1 + 3][temp2 + 3] == Main.UserC && PlayBoard.playBoard[temp1 + 4][temp2 + 4] == Main.UserC) {
	                                          superWeight[temp1 + 1][temp2 + 1] += 40; add++;
	                                          superWeight[temp1 + 2][temp2 + 2] += 40; add++;
	                                    }
	                                    else if (PlayBoard.playBoard[temp1 - 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 - 3][temp2 - 3] == 0 && PlayBoard.playBoard[temp1 - 4][temp2 - 4] == Main.UserC && PlayBoard.playBoard[temp1 - 5][temp2 - 5] == Main.UserC) {
	                                          superWeight[temp1 - 2][temp2 - 2] += 40; add++;
	                                          superWeight[temp1 - 3][temp2 - 3] += 40; add++;
	                                    }
	                                    else if (PlayBoard.playBoard[temp1 - 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 + 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 - 3][temp2 - 3] == Main.UserC && PlayBoard.playBoard[temp1 + 2][temp2 + 2] == Main.UserC) {
	                                          superWeight[temp1 - 2][temp2 - 2] += 40; add++;
	                                          superWeight[temp1 + 1][temp2 + 1] += 40; add++;
	                                    }
	                                    else if (PlayBoard.playBoard[temp1 - 4][temp2 - 4] == 0 && PlayBoard.playBoard[temp1 - 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 - 3][temp2 - 3] == Main.UserC && PlayBoard.playBoard[temp1 - 5][temp2 - 5] == Main.UserC) {
	                                          superWeight[temp1 - 4][temp2 - 4] += 40; add++;
	                                          superWeight[temp1 - 2][temp2 - 2] += 40; add++;
	                                    }
	                                    else if (PlayBoard.playBoard[temp1 + 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 - 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 + 3][temp2 + 3] == Main.UserC && PlayBoard.playBoard[temp1 + 2][temp2 + 2] == Main.UserC) {
	                                          superWeight[temp1 + 1][temp2 + 1] += 40; add++;
	                                          superWeight[temp1 - 2][temp2 - 2] += 40; add++;
	                                    }
	                               }
	                            } catch (ArrayIndexOutOfBoundsException e) {}
	                            temp1++;
	                            temp2++;
	                         } else myCount = 0;
	                 }catch(ArrayIndexOutOfBoundsException e) {}
	              }
	              }
	          }
	       
	   


	   if(add >= 2) return;
	// 5 오른쪽위에서 왼쪽아래(우대각/) 방어
	   for( int j = 0;j<19;j++){
	      myCount = 0;
	      for (int i = 5; i < 19; i++) {
	         int temp1 = i;
	         int temp2 = j;
	         for (int k = 0; k < 5; k++) {
	        	try {
	            if (PlayBoard.playBoard[temp1][temp2] == Main.UserC) {
	               myCount++;
	                  if (myCount == 5) {
	                	  if((temp1 - 1 < 0 || temp2 + 1 > 18) && PlayBoard.playBoard[temp1 + 5][temp2 - 5] == 0) {
								superWeight[temp1 + 5][temp2 - 5] += 40;
								add++;
							}
							else if((temp1 + 5 > 18 || temp2 - 5 < 0) && PlayBoard.playBoard[temp1 - 1][temp2 + 1] == 0){
								superWeight[temp1 - 1][temp2 + 1] += 40;
								add++;
							}
							else if (PlayBoard.playBoard[temp1 + 5][temp2 - 5] == 0
	                           && PlayBoard.playBoard[temp1 - 1][temp2 + 1] == 0) {
	                    	 superWeight[temp1 + 5][temp2 - 5] += 40; add++;
	                    	 superWeight[temp1 - 1][temp2 + 1] += 40; add++;
	                     } else if (PlayBoard.playBoard[temp1 + 4][temp2 - 4] == 0
	                           || PlayBoard.playBoard[temp1 - 1][temp2 + 1] == 0) {
	                        if (PlayBoard.playBoard[temp1 + 5][temp2 - 5] == 0) {
	                        	superWeight[temp1 + 5][temp2 - 5] += 40; add++;
	                        } else if (PlayBoard.playBoard[temp1 - 1][temp2 + 1] == 0) {
	                        	superWeight[temp1 - 1][temp2 + 1] += 40; add++;
	                        }
	                     }
	                  }
		                  temp1--;
		                  temp2++;
	                  } else myCount = 0;
	            } catch (ArrayIndexOutOfBoundsException e) {}
	         }
	      }
	   }
	   
	
	   
	   if(add >= 2) return;
	   // 4 오른쪽위에서 왼쪽아래(우대각/) 방어
	   for( int j = 0;j<19;j++){
	      myCount = 0;
	      for (int i = 5; i < 19; i++) {
	         int temp1 = i;
	         int temp2 = j;
	         for (int k = 0; k < 4; k++) {
	        	try {
	            if (PlayBoard.playBoard[temp1][temp2] == Main.UserC) {
	               myCount++;
	                  if (myCount == 4) {
	                	  if((temp1 - 1 < 0 || temp2 + 1 > 18) && PlayBoard.playBoard[temp1 + 4][temp2 - 4] == 0) {
								superWeight[temp1 + 4][temp2 - 4] += 40;
								add++;
							}
							else if((temp1 + 4 > 18 || temp2 - 4 < 0) && PlayBoard.playBoard[temp1 - 1][temp2 + 1] == 0){
								superWeight[temp1 - 1][temp2 + 1] += 40;
								add++;
							}
							else if (PlayBoard.playBoard[temp1 + 4][temp2 - 4] == 0
	                           && PlayBoard.playBoard[temp1 - 1][temp2 + 1] == 0) {
	                    	 superWeight[temp1 + 4][temp2 - 4] += 40; add++;
	                    	 superWeight[temp1 - 1][temp2 + 1] += 40; add++;
	                     } else if (PlayBoard.playBoard[temp1 + 4][temp2 - 4] == 0
	                           || PlayBoard.playBoard[temp1 - 1][temp2 + 1] == 0) {
	                        if (PlayBoard.playBoard[temp1 + 4][temp2 - 4] == 0
	                              && PlayBoard.playBoard[temp1 + 5][temp2 - 5] == 0) {
	                        	if(weight[temp1 + 4][temp2 - 4]>weight[temp1 + 5][temp2 - 5]) {
	                        		superWeight[temp1 + 4][temp2 - 4] += 40; add++;
	                        	}else{
	                        		superWeight[temp1 + 5][temp2 - 5] += 40; add++;
	                        	}
	                        } else if (PlayBoard.playBoard[temp1 - 1][temp2 + 1] == 0
	                              && PlayBoard.playBoard[temp1 - 2][temp2 + 2] == 0) {
	                        	if(weight[temp1 - 1][temp2 + 1]>weight[temp1 - 1][temp2 + 1]) {
	                        		superWeight[temp1 - 1][temp2 + 1] += 40; add++;
	                        	}else{
	                        		superWeight[temp1 - 1][temp2 + 1] += 40; add++;
	                        	}
	                        }
	                     }
	                  }
		                  temp1--;
		                  temp2++;
	                  } else  myCount = 0;
	        	} catch (ArrayIndexOutOfBoundsException e) {}
	         }
	      }
	   }
	   
	   if(add >= 2) return;
	   // 3(우대각/) 1공백 방어
	   for(int j = 0;j<19;j++){
	      myCount = 0;
	      for (int i = 0; i < 19; i++) {
	         int temp1 = i;
	         int temp2 = j;
	         for (int k = 0; k < 3; k++) {
	        	try {
	            if (PlayBoard.playBoard[temp1][temp2] == Main.UserC) {
	               myCount++;
	               
	                  if (myCount == 3) {
	                    if (PlayBoard.playBoard[temp1 - 1][temp2+ 1] == 0 && PlayBoard.playBoard[temp1 - 2][temp2 + 2] == Main.UserC && PlayBoard.playBoard[temp1 - 3][temp2+ 3] == 0) {
		                    if(weight[temp1 - 3][temp2+ 3] > weight[temp1 - 1][temp2+ 1]) {
		                    	superWeight[temp1 - 3][temp2+ 3] += 40; add++;
		                    }else {
		                       weight[temp1 - 1][temp2+ 1] += 40; add++;
		                    }
		                 } 
		                 else if (PlayBoard.playBoard[temp1 + 3][temp2- 3] == 0 && PlayBoard.playBoard[temp1 + 4][temp2- 4] == Main.UserC && PlayBoard.playBoard[temp1 + 5][temp2- 5] == 0) {
		                    if(weight[temp1 + 3][temp2- 3]  > weight[temp1 + 5][temp2 - 5]) {
		                    	superWeight[temp1+ 3][temp2 - 3] += 40; add++;}
		                    else {
		                    	superWeight[temp1 + 5][temp2 - 5] += 40; add++;}
		                    }
	                  
	                  }
		                  temp1--;
		                  temp2++;
	                  } else myCount = 0;
	        	} catch (ArrayIndexOutOfBoundsException e) {}
	         }
	      }
	   }
	   
	   if(add >= 2) return;
	   // 3(우대각/) 2공백 1 방어
	   for(int j = 0;j<19;j++){
	      myCount = 0;
	      for (int i = 0; i < 19; i++) {
	         int temp1 = i;
	         int temp2 = j;
	         for (int k = 0; k < 3; k++) {
	        	try {
	            if (PlayBoard.playBoard[temp1][temp2] == Main.UserC) {
	               myCount++;
	               
	                  if (myCount == 3) {
	                    if (PlayBoard.playBoard[temp1 - 1][temp2+ 1] == 0 && PlayBoard.playBoard[temp1 - 2][temp2 + 2] == 0 && PlayBoard.playBoard[temp1 - 3][temp2+ 3] == Main.UserC) {
		                    if(weight[temp1 - 2][temp2+ 2] > weight[temp1 - 1][temp2+ 1]) {
		                    	superWeight[temp1 - 2][temp2+ 2] += 40; add++;
		                    }else {
		                       weight[temp1 - 1][temp2+ 1] += 40; add++;
		                    }
		                 } 
		                 else if (PlayBoard.playBoard[temp1 + 3][temp2- 3] == 0 && PlayBoard.playBoard[temp1 + 4][temp2- 4] == 0 && PlayBoard.playBoard[temp1 + 5][temp2- 5] == Main.UserC) {
		                    if(weight[temp1 + 3][temp2- 3]  > weight[temp1 + 4][temp2 - 4]) {
		                    	superWeight[temp1+ 3][temp2 - 3] += 40; add++;}
		                    else {
		                    	superWeight[temp1 + 5][temp2 - 5] += 40; add++;}
		                    }
	                  
	                  }
		                  temp1--;
		                  temp2++;
	                  } else myCount = 0;
	        	} catch (ArrayIndexOutOfBoundsException e) {}
	         }
	      }
	   }
	      
	   
	   if(add >= 2) return;
	      // 2 (공백2) 2 방어 (우대각/)오른쪽 위에서 왼쪽 아래 
	      for (int i = 0; i < 19; i++) {
	          myCount = 0;
	          for (int j = 0; j < 19; j++) {
	             int temp1 = i;
	              int temp2 = j;
	              for (int k = 0; k < 2; k++) {
	                 try {
	                      if (PlayBoard.playBoard[temp1][temp2] == Main.UserC) {
	                            myCount++;
	                            try {
	                               if (myCount == 2) {
	                                   if (PlayBoard.playBoard[temp1 - 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 + 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 - 2][temp2 + 2] == Main.UserC && PlayBoard.playBoard[temp1 - 3][temp2 + 3] == Main.UserC) {
	                                       superWeight[temp1 - 1][temp2 + 1] += 40; add++;
	                                       superWeight[temp1 + 2][temp2 - 2] += 40; add++;
	                           
	                                    } 
	                                    else if (PlayBoard.playBoard[temp1 + 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 + 5][temp2 - 5] == 0 && PlayBoard.playBoard[temp1 + 3][temp2 - 3] == Main.UserC && PlayBoard.playBoard[temp1 + 4][temp2 - 4] == Main.UserC) {
	                                       superWeight[temp1 + 2][temp2 - 2] += 40; add++;
	                                       superWeight[temp1 + 5][temp2 - 5] += 40; add++;
	                                    }
	                                    else if (PlayBoard.playBoard[temp1 + 4][temp2 - 4] == 0 && PlayBoard.playBoard[temp1 + 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 + 3][temp2 - 3] == Main.UserC && PlayBoard.playBoard[temp1 + 5][temp2 - 5] == Main.UserC) {
	                                          superWeight[temp1 + 4][temp2 - 4] += 40; add++;
	                                          superWeight[temp1 + 2][temp2 - 2] += 40; add++;
	                                    }
	                                    else if (PlayBoard.playBoard[temp1 + 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 - 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 - 2][temp2 + 2] == Main.UserC && PlayBoard.playBoard[temp1 + 3][temp2 - 3] == Main.UserC) {
	                                          superWeight[temp1 + 2][temp2 - 2] += 40; add++;
	                                          superWeight[temp1 - 1][temp2 + 1] += 40; add++;
	                                    }
	                                    else if (PlayBoard.playBoard[temp1 + 3][temp2 - 3] == 0 && PlayBoard.playBoard[temp1 + 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 + 3][temp2 - 3] == Main.UserC && PlayBoard.playBoard[temp1 + 4][temp2 - 4] == Main.UserC) {
	                                          superWeight[temp1 + 3][temp2 - 3] += 40; add++;
	                                          superWeight[temp1 + 2][temp2 - 2] += 40; add++;
	                                    }
	                                    else if (PlayBoard.playBoard[temp1 - 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 - 2][temp2 + 2] == 0 && PlayBoard.playBoard[temp1 - 3][temp2 + 3] == Main.UserC && PlayBoard.playBoard[temp1 - 4][temp2 + 4] == Main.UserC) {
	                                          superWeight[temp1 - 1][temp2 + 1] += 40; add++;
	                                          superWeight[temp1 - 2][temp2 + 2] += 40; add++;
	                                    }
	                                    else if (PlayBoard.playBoard[temp1 + 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 + 4][temp2 - 4] == 0 && PlayBoard.playBoard[temp1 + 3][temp2 - 3] == Main.UserC && PlayBoard.playBoard[temp1 + 5][temp2 - 5] == Main.UserC) {
	                                          superWeight[temp1 + 2][temp2 - 2] += 40; add++;
	                                          superWeight[temp1 + 4][temp2 - 4] += 40; add++;
	                                    }
	                                    else if (PlayBoard.playBoard[temp1 + 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 + 5][temp2 - 5] == 0 && PlayBoard.playBoard[temp1 + 3][temp2 - 3] == Main.UserC && PlayBoard.playBoard[temp1 + 4][temp2 - 4] == Main.UserC) {
	                                          superWeight[temp1 + 2][temp2 - 2] += 40; add++;
	                                          superWeight[temp1 + 5][temp2 - 5] += 40; add++;
	                                    }
	                                    else if (PlayBoard.playBoard[temp1 - 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 + 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 - 2][temp2 + 2] == Main.UserC && PlayBoard.playBoard[temp1 - 3][temp2 + 3] == Main.UserC) {
	                                         superWeight[temp1 - 1][temp2 + 1] += 40; add++;
	                                         superWeight[temp1 + 2][temp2 - 2] += 40; add++;
	                                   }
	                               }
	                            } catch (ArrayIndexOutOfBoundsException e) {}
	                            temp1--;
	                            temp2++;
	                         } else myCount = 0;
	                 }catch(ArrayIndexOutOfBoundsException e) {}
	              }
	              }
	          }
	   
	   


	   if(add >= 2) return;
	   
	   ////본인 전개 플러스점수 ---------------------------------------------------------------------------------
	   //연결 양끝으로 연결갯수*2의 가중치 더하기 
	   
	   
	   
	   
	   //상대 방해 플러스점수 --------------------------------------------------------------------------------------
	   //연결 양끝으로 연결횟수만큼의 가중치 더하기
	   

	   
	   

   }
   

   
   // 일반가중치+특수가중치 판에서 최대 가중치를 찾아 x,y 값 저장해주기
   public static void returnPoint(int[][] board) {
	   
	   int max = 0;
	   for(int i = 0;i<19;i++){
	      for (int j = 0; j < 19; j++) {
	         if (superWeight[i][j]+weight[i][j] > max) {
	            max = superWeight[i][j]+weight[i][j];
	            alphago.x = i;
	            alphago.y = j;
	         }
	      }
	   }
	   
   }
   
    
   // 현재 가중치 상태 콘솔에 출력
   public static void showWeight() {
      for (int i = 0; i < 19; i++) {
         for (int j = 0; j < 19; j++) {
        	 System.out.printf("[%2d]", weight[j][i] );
         }
         System.out.println("");
      }
      System.out.println("");
   }
   
   
   
   static void testTest(int myCount, int add){
	   
	   

	//// 한방오리백숙 -------------------------------------------------------------------------------------


		  //세로 시작점 ----------------------------------------------------------------------
		     
		  if (add >= 2)
				return;
			// 공격/세로/5-1
			for (int i = 0; i < 19; i++) {
				myCount = 0;
				for (int j = 0; j < 19; j++) {
					try {
						if (PlayBoard.playBoard[i][j] == Main.UserC) {
							myCount++;
							if (myCount == 5) {
								// 양끝 중 아무 빈곳에 가중치 왕창 이벤트
								/*
								 * if (j - 5 < 0 && PlayBoard.playBoard[i][j + 1] == 0) { // |[11111]0 right
								 * superWeight[i][j + 1] += 40; add++; } else if (j + 1 > 18 &&
								 * PlayBoard.playBoard[i][j - 5] == 0) { // 0[11111]| left superWeight[i][j - 5]
								 * += 40; add++; } else
								 */ if (j - 5 > 0 && PlayBoard.playBoard[i][j - 5] == 0) { // 0[11111] left //made a change
									superWeight[i][j - 5] += 80;
									add++;
								} else if (j + 1 < 19 && PlayBoard.playBoard[i][j + 1] == 0) { // *[11111]0 right //also
																								// made a change
									superWeight[i][j + 1] += 80;
									add++;
								}

							}
						} else
							myCount = 0;
					} catch (ArrayIndexOutOfBoundsException e) {
					}
				}
			}

			if (add >= 2)
				return;
			// 공격/세로/1-4-1/2-4
			for (int i = 0; i < 19; i++) {
				myCount = 0;
				for (int j = 0; j < 19; j++) {
					try {
						if (PlayBoard.playBoard[i][j] == Main.UserC) {
							myCount++;

							if (myCount == 4) {

								/*
								 * if (j - 4 < 0 && PlayBoard.playBoard[i][j + 1] == 0 &&
								 * PlayBoard.playBoard[i][j + 2] == 0) { // |[1111]00 right superWeight[i][j +
								 * 1] += 40; superWeight[i][j + 2] += 40; add += 2; } else if (j + 1 > 18 &&
								 * PlayBoard.playBoard[i][j - 4] == 0 && PlayBoard.playBoard[i][j - 5] == 0) {
								 * // 00[1111]| left superWeight[i][j - 4] += 40; superWeight[i][j - 5] += 40;
								 * add += 2; } else
								 */ if (j - 4 > 0 && j + 1 < 19 && PlayBoard.playBoard[i][j - 4] == 0
										&& PlayBoard.playBoard[i][j + 1] == 0) { // 0[1111]0 mid
									superWeight[i][j - 4] += 80;
									superWeight[i][j + 1] += 80;
									add += 2;
								}

								else if (j - 5 < 0 && PlayBoard.playBoard[i][j - 4] == 0
										&& PlayBoard.playBoard[i][j - 5] == 0) { // 00[1111] left ??????????????
									superWeight[i][j - 4] += 80;
									superWeight[i][j - 5] += 80;
									add += 2;
								} else if (j + 2 < 19 && PlayBoard.playBoard[i][j + 1] == 0
										&& PlayBoard.playBoard[i][j + 2] == 0) { // [1111]00 right
									superWeight[i][j + 2] += 80;
									superWeight[i][j + 1] += 80;
									add += 2;
								}

							}
						} else
							myCount = 0;
					} catch (ArrayIndexOutOfBoundsException e) {
					}
				}
			}

			if (add >= 2)
				return;
			// 공격/세로/3-1-1-1/1-3-1-1
			for (int i = 0; i < 19; i++) {
				myCount = 0;
				for (int j = 0; j < 19; j++) {
					try {
						if (PlayBoard.playBoard[i][j] == Main.UserC) {
							myCount++;
							if (myCount == 3) { // 세번연속일때

								if (j + 3 < 19 && PlayBoard.playBoard[i][j + 1] == 0
										&& PlayBoard.playBoard[i][j + 2] == Main.UserC
										&& PlayBoard.playBoard[i][j + 3] == 0) { // [111]010 right

									superWeight[i][j + 1] += 40;
									superWeight[i][j + 3] += 40;
									add += 2;
								}

								else if (j - 5 > 0
										&& (PlayBoard.playBoard[i][j - 3] == 0 && PlayBoard.playBoard[i][j - 4] == Main.UserC
												&& PlayBoard.playBoard[i][j - 5] == 0)) { // 010[111] left

									superWeight[i][j - 3] += 40;
									superWeight[i][j - 5] += 40;
									add += 2;
								}

								else if (j - 3 > 0 && j + 2 < 19 && PlayBoard.playBoard[i][j - 3] == 0
										&& PlayBoard.playBoard[i][j + 1] == 0
										&& PlayBoard.playBoard[i][j + 2] == Main.UserC) { // 0[111]01 mid
									superWeight[i][j - 3] += 40;
									superWeight[i][j + 2] += 40;
									add += 2;
								} else if (j - 4 > 0 && j + 1 < 19 && PlayBoard.playBoard[i][j - 3] == 0
										&& PlayBoard.playBoard[i][j - 4] == Main.UserC
										&& PlayBoard.playBoard[i][j + 1] == 0) { // 10[111]0 mid
									superWeight[i][j - 3] += 40;
									superWeight[i][j + 1] += 40;
									add += 2;
								}
							} else
								myCount = 0;
						}
					} catch (ArrayIndexOutOfBoundsException e) {
					}
				}
			}

			if (add >= 2)
				return;
			// 공격/세로/3-2-1
			for (int i = 0; i < 19; i++) {
				myCount = 0;
				for (int j = 0; j < 19; j++) {
					try {
						if (PlayBoard.playBoard[i][j] == Main.UserC) {
							myCount++;
							if (myCount == 3) { // 세번연속일때

								if (j + 3 < 19 && (PlayBoard.playBoard[i][j + 1] == 0 && PlayBoard.playBoard[i][j + 2] == 0
										&& PlayBoard.playBoard[i][j + 3] == Main.UserC)) { // [111]001 right

									superWeight[i][j + 1] += 40;
									superWeight[i][j + 2] += 40;
									add += 2;
								}

								else if (j - 5 > 0
										&& (PlayBoard.playBoard[i][j - 3] == 0 && PlayBoard.playBoard[i][j - 4] == 0
												&& PlayBoard.playBoard[i][j - 5] == Main.UserC)) { // 100[111] left

									superWeight[i][j - 3] += 40;
									superWeight[i][j - 4] += 40;
									add += 2;

								}
							} else
								myCount = 0;
						}
					} catch (ArrayIndexOutOfBoundsException e) {
					}
				}
			}

			if (add >= 2)
				return;
			// 공격/세로/2-2-2/1-2-1-2/1-1-1-1-2/1-1-2-1-1
			for (int i = 0; i < 19; i++) {
				myCount = 0;
				for (int j = 0; j < 19; j++) {
					// try { WHY????????????
					if (PlayBoard.playBoard[i][j] == Main.UserC) {
						myCount++;
						if (myCount == 2) {

							try {

								if (j + 4 < 19 && PlayBoard.playBoard[i][j + 1] == 0 && PlayBoard.playBoard[i][j + 2] == 0
										&& PlayBoard.playBoard[i][j + 3] == Main.UserC
										&& PlayBoard.playBoard[i][j + 4] == Main.UserC) { // [11]0011 right
									superWeight[i][j + 1] += 40;
									superWeight[i][j + 2] += 40;
									add += 2;
								} /*
									 * else if (j - 5 > 0 && PlayBoard.playBoard[i][j - 2] == 0 &&
									 * PlayBoard.playBoard[i][j - 3] == 0 && PlayBoard.playBoard[i][j - 4] ==
									 * Main.UserC && PlayBoard.playBoard[i][j - 5] == Main.UserC) { // 1100[11] left
									 * superWeight[i][j - 2] += 40; superWeight[i][j - 3] += 40; add += 2; }
									 */ else if (j - 2 > 0 && j + 3 > 19 && PlayBoard.playBoard[i][j - 2] == 0
										&& PlayBoard.playBoard[i][j + 1] == 0 && PlayBoard.playBoard[i][j + 2] == Main.UserC
										&& PlayBoard.playBoard[i][j + 3] == Main.UserC) { // 0[11]011 mid
									superWeight[i][j - 2] += 40;
									superWeight[i][j + 1] += 40;
									add += 2;
								} /*
									 * else if (j - 5 > 0 && PlayBoard.playBoard[i][j - 2] == 0 &&
									 * PlayBoard.playBoard[i][j - 5] == 0 && PlayBoard.playBoard[i][j - 3] ==
									 * Main.UserC && PlayBoard.playBoard[i][j - 4] == Main.UserC) { // 0110[11] left
									 * superWeight[i][j - 2] += 40; superWeight[i][j - 5] += 40; add += 2; }
									 */ else if (j - 5 > 0 && PlayBoard.playBoard[i][j - 2] == 0
										&& PlayBoard.playBoard[i][j - 4] == 0 && PlayBoard.playBoard[i][j - 3] == Main.UserC
										&& PlayBoard.playBoard[i][j - 5] == Main.UserC) { // 1010[11] left
									superWeight[i][j - 2] += 40;
									superWeight[i][j - 4] += 40;
									add += 2;
								} else if (j - 3 > 0 && j + 2 < 19 && PlayBoard.playBoard[i][j - 2] == 0
										&& PlayBoard.playBoard[i][j + 1] == 0 && PlayBoard.playBoard[i][j - 3] == Main.UserC
										&& PlayBoard.playBoard[i][j + 2] == Main.UserC) { // 10[11]01 mid
									superWeight[i][j - 2] += 40;
									superWeight[i][j + 1] += 40;
									add += 2;
								} else if (j + 4 < 19 && PlayBoard.playBoard[i][j + 1] == 0
										&& PlayBoard.playBoard[i][j + 3] == 0 && PlayBoard.playBoard[i][j + 2] == Main.UserC
										&& PlayBoard.playBoard[i][j + 4] == Main.UserC) { // [11]0101 right
									superWeight[i][j + 3] += 40;
									superWeight[i][j + 1] += 40;
									add += 2;
								} else if (j + 4 < 19 && PlayBoard.playBoard[i][j + 1] == 0
										&& PlayBoard.playBoard[i][j + 4] == 0 && PlayBoard.playBoard[i][j + 2] == Main.UserC
										&& PlayBoard.playBoard[i][j + 3] == Main.UserC) { // [11]0110 right
									superWeight[i][j + 4] += 40;
									superWeight[i][j + 1] += 40;
									add += 2;
								} /*
									 * else if (j - 4 > 0 && j + 1 < 19 && PlayBoard.playBoard[i][j + 1] == 0 &&
									 * PlayBoard.playBoard[i][j - 2] == 0 && PlayBoard.playBoard[i][j - 3] ==
									 * Main.UserC && PlayBoard.playBoard[i][j - 4] == Main.UserC) { // 110[11]0 mid
									 * superWeight[i][j - 2] += 40; superWeight[i][j + 1] += 40; add += 2; }
									 */
							} catch (ArrayIndexOutOfBoundsException e) {}
						}
					} else myCount = 0;
					// }catch (ArrayIndexOutOfBoundsException e) {}
				}
			}

		     

			//가로 시작점 -------------------------------------------------------------------------------------------------

			if (add >= 2)
				return;
			// 공격/가로/5-1
			for (int j = 0; j < 19; j++) {
				myCount = 0;
				for (int i = 0; i < 19; i++) {
					try {
						if (PlayBoard.playBoard[i][j] == Main.UserC) {
							myCount++;
							if (myCount == 5) { // 양끝 중 아무 빈곳에 가중치 왕창 이벤트
								/*
								 * if (i - 5 < 0 && PlayBoard.playBoard[i + 1][j] == 0) { // |[11111]0 (right) ?
								 * superWeight[i + 1][j] += 40; add++; }
								 * 
								 * else if (i + 1 > 18 && PlayBoard.playBoard[i - 5][j] == 0) { // 0[11111]|
								 * (left) ? superWeight[i - 5][j] += 40; add++; }
								 */

								/* else */ if (i - 5 > 0 && PlayBoard.playBoard[i - 5][j] == 0) { // 0[11111] (left)
									superWeight[i - 5][j] += 80;
									add++;
								}

								else if (i + 1 < 19 && PlayBoard.playBoard[i + 1][j] == 0) { // *[11111]0 (right)
									superWeight[i + 1][j] += 80;
									add++;
								}
							}
						} else
							myCount = 0;
					} catch (ArrayIndexOutOfBoundsException e) {
					}
				}
			}

			if (add >= 2)
				return;
			// 공격/가로/4-2/1-4-1
			for (int j = 0; j < 19; j++) {
				myCount = 0;
				for (int i = 0; i < 19; i++) {
					try {
						if (PlayBoard.playBoard[i][j] == Main.UserC) {
							myCount++;
							if (myCount == 4) {

								/*
								 * if (i - 4 < 0 && PlayBoard.playBoard[i + 1][j] == 0 && PlayBoard.playBoard[i
								 * + 2][j] == 0) { // |[1111]00 right //? superWeight[i + 1][j] += 40;
								 * superWeight[i + 2][j] += 40; add += 2; }
								 * 
								 * else if (i + 1 > 18 && PlayBoard.playBoard[i - 5][j] == 0 &&
								 * PlayBoard.playBoard[i - 4][j] == 0) { // 00[1111]| //(left) //? superWeight[i
								 * - 5][j] += 40; superWeight[i - 4][j] += 40; add += 2; }
								 */

								/* else */ if (i - 4 > 0 && i + 1 < 19 && PlayBoard.playBoard[i - 4][j] == 0
										&& PlayBoard.playBoard[i + 1][j] == 0) { // 0[1111]0 (mid)
									superWeight[i - 4][j] += 80;
									superWeight[i + 1][j] += 80;
									add += 2;
								}

								if (i - 5 > 0 && PlayBoard.playBoard[i - 4][j] == 0 && PlayBoard.playBoard[i - 5][j] == 0) { // 00[1111]
																																// left
									superWeight[i - 4][j] += 80;
									superWeight[i - 5][j] += 80;
									add += 2;
								} else if (i + 2 < 19 && PlayBoard.playBoard[i + 1][j] == 0
										&& PlayBoard.playBoard[i + 2][j] == 0) { // [1111]00 right
									superWeight[i + 2][j] += 80;
									superWeight[i + 1][j] += 80;
									add += 2;
								}

							}
						} else
							myCount = 0;
					} catch (ArrayIndexOutOfBoundsException e) {
					}
				}
			}

			if (add >= 2)
				return;
			// 공격/가로/3-1-1-1
			for (int j = 0; j < 19; j++) {
				myCount = 0;
				for (int i = 0; i < 19; i++) {
					try {
						if (PlayBoard.playBoard[i][j] == Main.UserC) {
							myCount++;
							if (myCount == 3) {
								if (i + 3 < 19 && PlayBoard.playBoard[i + 1][j] == 0
										&& PlayBoard.playBoard[i + 2][j] == Main.UserC
										&& PlayBoard.playBoard[i + 3][j] == 0) { // [111]010 right

									superWeight[i + 3][j] += 40;
									superWeight[i + 1][j] += 40;
									add += 2;
								} else if (i - 5 > 0 && PlayBoard.playBoard[i - 3][j] == 0
										&& PlayBoard.playBoard[i - 4][j] == Main.UserC
										&& PlayBoard.playBoard[i - 5][j] == 0) { // 010[111] left

									superWeight[i - 3][j] += 40;
									superWeight[i - 5][j] += 40;
									add += 2;
								}
							}
						} else
							myCount = 0;
					} catch (ArrayIndexOutOfBoundsException e) {
					}
				}
			}

			if (add >= 2)
				return;
			// 공격/가로/3-2-1/1-3-1-1
			for (int j = 0; j < 19; j++) {
				myCount = 0;
				for (int i = 0; i < 19; i++) {
					try {
						if (PlayBoard.playBoard[i][j] == Main.UserC) {
							myCount++;
							if (myCount == 3) {
								if (i + 3 < 19 && PlayBoard.playBoard[i + 1][j] == 0 && PlayBoard.playBoard[i + 2][j] == 0
										&& PlayBoard.playBoard[i + 3][j] == Main.UserC) { // [111]001 right
									superWeight[i + 1][j] += 40;
									superWeight[i + 2][j] += 40;
									add += 2;
								} else if (i - 5 > 0 && PlayBoard.playBoard[i - 3][j] == 0
										&& PlayBoard.playBoard[i - 4][j] == 0
										&& PlayBoard.playBoard[i - 5][j] == Main.UserC) {// 100[111] left

									superWeight[i - 3][j] += 40;
									superWeight[i - 4][j] += 40;
									add += 2;
								}
								
								else if (i - 3 > 0 && i + 2 < 19 && PlayBoard.playBoard[i - 3][j] == 0
										&& PlayBoard.playBoard[i + 1][j] == 0
										&& PlayBoard.playBoard[i + 2][j] == Main.UserC) { // 0[111]01 mid
									superWeight[i - 3][j] += 40;
									superWeight[i + 2][j] += 40;
									add += 2;
								} else if (i - 4 > 0 && i + 1 < 19 && PlayBoard.playBoard[i - 3][j] == 0
										&& PlayBoard.playBoard[i - 4][j] == Main.UserC
										&& PlayBoard.playBoard[i + 1][j] == 0) { // 10[111]0 mid
									superWeight[i - 3][j] += 40;
									superWeight[i + 1][j] += 40;
									add += 2;
								}
							}
						} else
							myCount = 0;
					} catch (ArrayIndexOutOfBoundsException e) {
					}
				}
			}

			if (add >= 2)
				return;
			// 공격/가로/2-2-2/1-2-1-2/1-1-1-1-2/1-1-2-1-1
			for (int j = 0; j < 19; j++) {
				myCount = 0; // initialize myCount when entering new row
				for (int i = 0; i < 19; i++) {
					try {
						if (PlayBoard.playBoard[i][j] == Main.UserC) {
							myCount++;
							if (myCount == 2) {
								if (i + 4 < 19 && PlayBoard.playBoard[i + 1][j] == 0 && PlayBoard.playBoard[i + 2][j] == 0
										&& PlayBoard.playBoard[i + 3][j] == Main.UserC
										&& PlayBoard.playBoard[i + 4][j] == Main.UserC) { // [11]0011 (right)
									superWeight[i + 1][j] += 40;
									superWeight[i + 2][j] += 40;
									add += 2;
								}
								/*
								 * else if (i - 5 > 0 && PlayBoard.playBoard[i - 2][j] == 0 &&
								 * PlayBoard.playBoard[i - 3][j] == 0 && PlayBoard.playBoard[i - 4][j] ==
								 * Main.UserC && PlayBoard.playBoard[i - 5][j] == Main.UserC) { // 1100[11] left
								 * // same as above superWeight[i - 2][j] += 40; superWeight[i - 3][j] += 40;
								 * add += 2; }
								 */
								else if (i - 2 > 0 && i + 3 < 19 && PlayBoard.playBoard[i - 2][j] == 0
										&& PlayBoard.playBoard[i + 1][j] == 0 && PlayBoard.playBoard[i + 2][j] == Main.UserC
										&& PlayBoard.playBoard[i + 3][j] == Main.UserC) { // 0[11]011 (mid)
									superWeight[i - 2][j] += 40;
									superWeight[i + 1][j] += 40;
									add += 2;
								}

								/*
								 * else if (i - 5 > 0 && PlayBoard.playBoard[i - 2][j] == 0 &&
								 * PlayBoard.playBoard[i - 5][j] == 0 && PlayBoard.playBoard[i - 3][j] ==
								 * Main.UserC && PlayBoard.playBoard[i - 4][j] == Main.UserC) { // 0110[11] (left)
								 * // same as above superWeight[i - 2][j] += 40; superWeight[i - 5][j] += 40;
								 * add += 2; }
								 */

								else if (i - 5 > 0 && PlayBoard.playBoard[i - 2][j] == 0
										&& PlayBoard.playBoard[i - 4][j] == 0 && PlayBoard.playBoard[i - 3][j] == Main.UserC
										&& PlayBoard.playBoard[i - 5][j] == Main.UserC) { // 1010[11] (left)
									superWeight[i - 2][j] += 40;
									superWeight[i - 4][j] += 40;
									add += 2;
								}

								else if (i - 3 > 0 && i + 2 < 19 && PlayBoard.playBoard[i - 2][j] == 0
										&& PlayBoard.playBoard[i + 1][j] == 0 && PlayBoard.playBoard[i - 3][j] == Main.UserC
										&& PlayBoard.playBoard[i + 2][j] == Main.UserC) { // 10[11]01 mid
									superWeight[i - 2][j] += 40;
									superWeight[i + 1][j] += 40;
									add += 2;
								}

								else if (i + 4 < 19 && PlayBoard.playBoard[i + 1][j] == 0
										&& PlayBoard.playBoard[i + 3][j] == 0 && PlayBoard.playBoard[i + 2][j] == Main.UserC
										&& PlayBoard.playBoard[i + 4][j] == Main.UserC) { // [11]0101 (right)
									superWeight[i + 3][j] += 40;
									superWeight[i + 1][j] += 40;
									add += 2;
								}

								else if (i + 4 < 19 && PlayBoard.playBoard[i + 1][j] == 0
										&& PlayBoard.playBoard[i + 4][j] == 0 && PlayBoard.playBoard[i + 2][j] == Main.UserC
										&& PlayBoard.playBoard[i + 3][j] == Main.UserC) { // [11]0110 (right)
									superWeight[i + 4][j] += 40;
									superWeight[i + 1][j] += 40;
									add += 2;
								}

								/*
								 * else if (i - 4 > 0 && i + 1 < 19 && PlayBoard.playBoard[i + 1][j] == 0 &&
								 * PlayBoard.playBoard[i - 2][j] == 0 && PlayBoard.playBoard[i - 3][j] ==
								 * Main.UserC && PlayBoard.playBoard[i - 4][j] == Main.UserC) { // 110[11]0 (mid)
								 * //same as above superWeight[i - 2][j] += 40; superWeight[i + 1][j] += 40;
								 * add++; }
								 */
							}
						} else
							myCount = 0;
					} catch (ArrayIndexOutOfBoundsException e) {
					}
				}
			}
		        
		        
		  
		//좌대각 시작점 ----------------------------------------------------------------------

		     
		     
		     if(add >= 2) return;
		  // 5 왼쪽 위에서 오른쪽 아래(좌대각\) 공격 
		     for (int i = 0; i < 19; i++) {
		        myCount = 0;
		        for (int j = 0; j < 19; j++) {
		           int temp1 = i;
		           int temp2 = j;
		           for (int k = 0; k < 5; k++) {
		           	try {
		               if (PlayBoard.playBoard[temp1][temp2] == Main.UserC) {
		                 myCount++;
		                    if (myCount == 5) {
		                   	if((temp1 - 5 < 0 || temp2 - 5 < 0) && PlayBoard.playBoard[temp1 + 1][temp2 + 1] == 0) {
									superWeight[temp1 + 1][temp2 + 1] += 40; add++;
								}else if((temp1 + 1 > 18 || temp2 + 1 > 18) && PlayBoard.playBoard[temp1 - 5][temp2 - 5] == 0){
									superWeight[temp1 - 5][temp2 - 5] += 40; add++;
								}else if ((temp1 - 5 < 0 || temp2 - 5 < 0) && PlayBoard.playBoard[temp1 - 5][temp2 - 5] == 0) {
		                       	superWeight[temp1 - 5][temp2 - 5] += 80; add++;
		                       }else if ((temp1 + 1 > 18 || temp2 + 1 > 18) && PlayBoard.playBoard[temp1 + 1][temp2 + 1] == 0) {
		                       	superWeight[temp1 + 1][temp2 + 1] += 80; add++;
		                       } 
		                    }
		                 temp1++;
		                 temp2++;
		              } else myCount = 0;
		              } catch (ArrayIndexOutOfBoundsException e) {}
		           }
		        }
		     }
		     
		     
		     
		     if(add >= 2) return;
		     // 4 왼쪽 위에서 오른쪽 아래(좌대각\) 공격
		     for (int i = 0; i < 19; i++) {
		        myCount = 0;
		        for (int j = 0; j < 19; j++) {
		           int temp1 = i;
		           int temp2 = j;
		           for (int k = 0; k < 4; k++) {
		           	try {
			                if (PlayBoard.playBoard[temp1][temp2] == Main.UserC) {
			                  myCount++;
			                     if (myCount == 4) {
			                    	 if((temp1 - 4 <= 0 || temp2 - 4 <= 0) && (temp1 + 2 <= 18 && temp2 +2 <= 18) && PlayBoard.playBoard[temp1 + 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 + 2][temp2 + 2] == 0) {
										superWeight[temp1 + 1][temp2 + 1] += 40; add++;
										superWeight[temp1 + 2][temp2 + 2] += 40; add++;
									}else if((temp1 + 1 >= 18 || temp2 + 1 >= 18) && (temp1 - 5 >= 0 && temp2 - 5 >= 0) && PlayBoard.playBoard[temp1 - 5][temp2 - 5] == 0 && PlayBoard.playBoard[temp1 - 4][temp2 - 4] == 0){
										superWeight[temp1 - 5][temp2 - 5] += 40; add++;
										superWeight[temp1 - 4][temp2 - 4] += 40; add++;
									}else if ((temp1 -4 > 0 && temp2 -4 > 0) && (temp1 + 1 < 19 && temp2 + 1 < 19) && PlayBoard.playBoard[temp1 - 4][temp2 - 4] == 0 && PlayBoard.playBoard[temp1 + 1][temp2 + 1] == 0) {
			                        	superWeight[temp1 - 4][temp2 - 4] += 80; add++;
			                        	superWeight[temp1 + 1][temp2 + 1] += 80; add++;
			                        } else if ((temp1 - 5 >= 0 && temp2 - 5 >= 0) && (PlayBoard.playBoard[temp1 - 4][temp2 - 4] == 0 && PlayBoard.playBoard[temp1 - 5][temp2 - 5] == 0)) {
		                       	   	superWeight[temp1 - 4][temp2 - 4] += 80; add++;
		                       	   	superWeight[temp1 - 5][temp2 - 5] += 80; add++;
		                          } else if ((temp1 + 2 <= 18 && temp2 +2 <= 18) && (PlayBoard.playBoard[temp1 + 1][temp2 + 1] == 0&& PlayBoard.playBoard[temp1 + 2][temp2 + 2] == 0)) {
		                       	    superWeight[temp1 + 1][temp2 + 1] += 80; add++;
		                       	    superWeight[temp1 + 2][temp2 + 2] += 80; add++;
		                          }
			                     }
			                  temp1++;
			                  temp2++;
			               } else myCount = 0;
		              } catch (ArrayIndexOutOfBoundsException e) {}
		           }
		        }
		     }
		     

			   if(add >= 2) return;
			   // 3(좌대각\) 1공백 공격
			   for(int i = 0;i<19;i++){
			      myCount = 0;
			      for (int j = 0; j < 19; j++) {
			         int temp1 = i;
			         int temp2 = j;
			         for (int k = 0; k < 3; k++) {
			        	 try {
				             if (PlayBoard.playBoard[temp1][temp2] == Main.UserC) {
				               myCount++;
				                  if (myCount == 3) {
				                    if ((temp1 + 3 <= 18 && temp2 + 3 <= 18) && PlayBoard.playBoard[temp1 + 1][temp2+ 1] == 0 && PlayBoard.playBoard[temp1 + 2][temp2 + 2] == Main.UserC && PlayBoard.playBoard[temp1 + 3][temp2+ 3] == 0) {
										superWeight[temp1 + 1][temp2+ 1] += 40; add++;
				                    	superWeight[temp1 + 3][temp2+ 3] += 40; add++;
				                    	
					                 }else if ((temp1 - 5 >= 0 && temp2 - 5 >= 0) && PlayBoard.playBoard[temp1 - 3][temp2- 3] == 0 && PlayBoard.playBoard[temp1 - 4][temp2- 4] == Main.UserC && PlayBoard.playBoard[temp1 - 5][temp2- 5] == 0) {
					                	superWeight[temp1 - 5][temp2 - 5] += 40; add++;
				                    	superWeight[temp1 - 3][temp2 - 3] += 40; add++;
					                 }
					               temp1++;
					               temp2++;
				                  } else myCount = 0;
				             }
			            } catch (ArrayIndexOutOfBoundsException e) {}
			           }
			         
			      }
			   }
			   
			   
			   if(add >= 2) return;
			   // 3(좌대각\) 2공백 1 공격
			   for(int i = 0;i<19;i++){
			      myCount = 0;
			      for (int j = 0; j < 19; j++) {
			         int temp1 = i;
			         int temp2 = j;
			         for (int k = 0; k < 3; k++) {
			        	try {
				            if (PlayBoard.playBoard[temp1][temp2] == Main.UserC) {
				               myCount++;
				               if (myCount == 3) {
				                    if ((temp1 + 3 <= 18 && temp2 + 3 <= 18) && PlayBoard.playBoard[temp1 + 1][temp2+ 1] == 0 && PlayBoard.playBoard[temp1 + 2][temp2 + 2] == 0 && PlayBoard.playBoard[temp1 + 3][temp2+ 3] == Main.UserC) {
										superWeight[temp1 + 1][temp2+ 1] += 40; add++;
				                    	superWeight[temp1 + 2][temp2+ 2] += 40; add++;
				                    	
					                 }else if ((temp1 - 5 >= 0 && temp2 - 5 >= 0) && PlayBoard.playBoard[temp1 - 3][temp2- 3] == 0 && PlayBoard.playBoard[temp1 - 4][temp2- 4] == 0 && PlayBoard.playBoard[temp1 - 5][temp2- 5] == Main.UserC) {
					                	superWeight[temp1 - 4][temp2 - 4] += 40; add++;
				                    	superWeight[temp1 - 3][temp2 - 3] += 40; add++;
					                 }
					               temp1++;
					               temp2++;
				                  } else myCount = 0;
				            	}
			        	 }catch (ArrayIndexOutOfBoundsException e) {}
			         }
			      }
			   }
			   

			   if(add >= 2) return;
			      // 2 (공백2) 2 공격 (좌대각\)왼쪽 위에서 오른쪽 아래
			      for (int i = 0; i < 19; i++) {
			          myCount = 0;
			          for (int j = 0; j < 19; j++) {
			             int temp1 = i;
			              int temp2 = j;
			              for (int k = 0; k < 2; k++) {
			                 try {
			                      if (PlayBoard.playBoard[temp1][temp2] == Main.UserC) {
			                            myCount++;
			                            try {
			                               if (myCount == 2) {
			                            	   //101011(up)
			                                   if ((temp1 - 5 >= 0 && temp2 - 5 >= 0 )&& PlayBoard.playBoard[temp1 - 4][temp2 - 4] == 0 && PlayBoard.playBoard[temp1 - 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 - 3][temp2 - 3] == Main.UserC && PlayBoard.playBoard[temp1 - 5][temp2 - 5] == Main.UserC) {
			                                          superWeight[temp1 - 4][temp2 - 4] += 40; add++;
			                                          superWeight[temp1 - 2][temp2 - 2] += 40; add++;
			                                    }
			                                   //110011(up)
			                                    else if ((temp1 - 5 >= 0 && temp2 - 5 >= 0 )&& PlayBoard.playBoard[temp1 - 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 - 3][temp2 - 3] == 0 && PlayBoard.playBoard[temp1 - 4][temp2 - 4] == Main.UserC && PlayBoard.playBoard[temp1 - 5][temp2 - 5] == Main.UserC) {
			                                          superWeight[temp1 - 2][temp2 - 2] += 40; add++;
			                                          superWeight[temp1 - 3][temp2 - 3] += 40; add++;
			                                    }
			                                   //011011(up)
			                                    else if ((temp1 - 5 >= 0 && temp2 - 5 >= 0 )&& PlayBoard.playBoard[temp1 - 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 - 5][temp2 - 5] == 0 && PlayBoard.playBoard[temp1 - 4][temp2 - 4] == Main.UserC && PlayBoard.playBoard[temp1 - 3][temp2 - 3] == Main.UserC) {
			                                          superWeight[temp1 - 2][temp2 - 2] += 40; add++;
			                                          superWeight[temp1 - 5][temp2 - 5] += 40; add++;
			                                    }
			                                   //110110(mid)
			                                    else if ((temp1 - 4 >= 0 && temp2 - 4 >= 0 && temp1 + 1 <= 18 && temp2 + 1 <= 18) && PlayBoard.playBoard[temp1 + 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 - 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 - 3][temp2 - 3] == Main.UserC && PlayBoard.playBoard[temp1 - 4][temp2 - 4] == Main.UserC) {
			                                       superWeight[temp1 + 1][temp2 + 1] += 40; add++;
			                                       superWeight[temp1 - 2][temp2 - 2] += 40; add++;
			                                    }
			                                   //011011(mid)
			                                    else if ((temp1 - 2 >= 0 && temp2 - 2 >= 0 && temp1 + 3 <= 18 && temp2 + 3 <= 18) && PlayBoard.playBoard[temp1 - 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 + 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 + 2][temp2 + 2] == Main.UserC && PlayBoard.playBoard[temp1 + 3][temp2 + 3] == Main.UserC) {
			                                          superWeight[temp1 - 2][temp2 - 2] += 40; add++;
			                                          superWeight[temp1 + 1][temp2 + 1] += 40; add++;
			                                    }
			                                   //110110(mid)
			                                    else if ((temp1 - 4 >= 0 && temp2 - 4 >= 0 && temp1 + 1 <= 18 && temp2 + 1 <= 18) && PlayBoard.playBoard[temp1 + 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 - 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 - 3][temp2 - 3] == Main.UserC && PlayBoard.playBoard[temp1 - 4][temp2 - 4] == Main.UserC) {
			                                       superWeight[temp1 + 1][temp2 + 1] += 40; add++;
			                                       superWeight[temp1 - 2][temp2 - 2] += 40; add++;
			                                   }
			                                   //110110(down)
				                                   else if ((temp1- 4 <= 18 && temp2 - 4 <= 18) && PlayBoard.playBoard[temp1 + 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 + 4][temp2 + 4] == 0 && PlayBoard.playBoard[temp1 + 2][temp2 + 2] == Main.UserC && PlayBoard.playBoard[temp1 + 3][temp2 + 3] == Main.UserC) {
				                                       superWeight[temp1 + 1][temp2 + 1] += 40; add++;
				                                       superWeight[temp1 + 4][temp2 + 4] += 40; add++;
				                               }
			                                   //110011(down)
			                                    else if ((temp1- 4 <= 18 && temp2 - 4 <= 18) && PlayBoard.playBoard[temp1 + 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 + 2][temp2 + 2] == 0 && PlayBoard.playBoard[temp1 + 3][temp2 + 3] == Main.UserC && PlayBoard.playBoard[temp1 + 4][temp2 + 4] == Main.UserC) {
			                                          superWeight[temp1 + 1][temp2 + 1] += 40; add++;
			                                          superWeight[temp1 + 2][temp2 + 2] += 40; add++;
			                                   }
			                                   //110101(down)
			                                    else if ((temp1- 4 <= 18 && temp2 - 4 <= 18) && PlayBoard.playBoard[temp1 + 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 + 4][temp2 + 4] == 0 && PlayBoard.playBoard[temp1 + 3][temp2 + 3] == Main.UserC && PlayBoard.playBoard[temp1 + 2][temp2 + 2] == Main.UserC) {
			                                          superWeight[temp1 + 1][temp2 + 1] += 40; add++;
			                                          superWeight[temp1 + 4][temp2 + 4] += 40; add++;
			                                   }
			                               }
			                            } catch (ArrayIndexOutOfBoundsException e) {}
			                            temp1++;
			                            temp2++;
			                         } else myCount = 0;
			                 }
			                 catch(ArrayIndexOutOfBoundsException e) {}
			              }
			              }
			          }
			      
			      
			      //우대각 시작점 -----------------------------------------------------------------------------------------
			       
		 if(add >= 2) return;
		 // 5 오른쪽위에서 왼쪽아래 (우대각/) 공격 
		     for (int j = 0; j < 19; j++) {
		        myCount = 0;
		        for (int i = 0; i < 19; i++) {
		           int temp1 = i;
		           int temp2 = j;
		           for (int k = 0; k < 5; k++) {
		           	try {
		           	if (PlayBoard.playBoard[temp1][temp2] == Main.UserC) {
		           		myCount++;
		                    if (myCount == 5) {
		                   	 //우상단 막히고 좌하단 뚫림
		                   	if((temp1 + 5 > 18 || temp2 - 5 < 0) && (temp1 - 1 >= 0 || temp2 + 1 <= 18) && PlayBoard.playBoard[temp1 - 1][temp2 + 1] == 0) {
									superWeight[temp1 - 1][temp2 + 1] += 40; add++;
								}
		                   	//좌하단 막히고 우상단 뚫림
								else if((temp1 - 1 < 0 || temp2 + 1 > 18) && (temp1 + 5 <= 18 && temp2 - 5 >= 0) && PlayBoard.playBoard[temp1 + 5][temp2 - 5] == 0){
									superWeight[temp1 + 5][temp2 - 5] += 40; add++;
								}
		                   	//우상단 자리있음
								else if ((temp1 + 5 <= 18 && temp2 - 5 >= 0) && PlayBoard.playBoard[temp1 + 5][temp2 - 5] == 0) {
		                       	superWeight[temp1 + 5][temp2 - 5] += 80;  add++;
		                       } 
		                   	//좌하단 자리있음
								else if ((temp1 - 1 >= 0 || temp2 + 1 <= 18) && PlayBoard.playBoard[temp1 - 1][temp2 + 1] == 0) {
		                       	superWeight[temp1 - 1][temp2 + 1] += 80; add++;
		                       }
		                    }
		                    temp1--;
		                    temp2++;
		               } else myCount = 0;
		              } catch (ArrayIndexOutOfBoundsException e) {}
		           }
		        }
		     }
		     
		     
		     
		     if(add >= 2) return;
		     // 4 오른쪽위에서 왼쪽아래 (우대각/) 공격
		     for (int j = 0; j < 19; j++) {
		        myCount = 0;
		        for (int i = 0; i < 19; i++) {
		           int temp1 = i;
		           int temp2 = j;
		           for (int k = 0; k < 4; k++) {
		           	try {
			                if (PlayBoard.playBoard[temp1][temp2] == Main.UserC) {
			                  myCount++;
			                     if (myCount == 4) {
			                    	 //우상단 막히고 좌하단에 두개
			                    	 if((temp1 + 4 > 18 || temp2 - 4 < 0) && (temp1 - 2 >= 0 && temp2 + 2 <= 18) && PlayBoard.playBoard[temp1 - 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 - 2][temp2 + 2] == 0) {
										superWeight[temp1 - 1][temp2 + 1] += 40; add++;
										superWeight[temp1 - 2][temp2 + 2] += 40; add++;
									//촤하단 막히고 우상단에 두개
									}else if((temp1 + 1 > 18 || temp2 - 1 < 0) && (temp1 + 5 >= 0 && temp2 - 5 >= 0) && PlayBoard.playBoard[temp1 + 5][temp2 - 5] == 0 && PlayBoard.playBoard[temp1 + 4][temp2 - 4] == 0){
										superWeight[temp1 + 5][temp2 - 5] += 40; add++;
										superWeight[temp1 + 4][temp2 - 4] += 40; add++;
									//우상단 좌하단에 하나씩 뚫림 
									}else if ((temp1 + 4 <= 18 && temp2 - 4 >= 0) && (temp1 - 1 >= 0 && temp2 + 1 <= 18) && PlayBoard.playBoard[temp1 + 4][temp2 - 4] == 0 && PlayBoard.playBoard[temp1 - 1][temp2 + 1] == 0) {
			                        	superWeight[temp1 + 4][temp2 - 4] += 80; add++;
			                        	superWeight[temp1 - 1][temp2 + 1] += 80; add++;
			                        //우상단에 두개
			                        } else if ((temp1 + 5 >= 0 && temp2 - 5 >= 0) && PlayBoard.playBoard[temp1 + 5][temp2 - 5] == 0 && PlayBoard.playBoard[temp1 + 4][temp2 - 4] == 0) {
			                        	superWeight[temp1 + 5][temp2 - 5] += 40; add++;
										superWeight[temp1 + 4][temp2 - 4] += 40; add++;
		                       	//좌하단에 두개
		                          } else if ((temp1 - 2 >= 0 && temp2 +2 <= 18) && PlayBoard.playBoard[temp1 - 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 - 2][temp2 + 2] == 0) {
		                       	    superWeight[temp1 - 1][temp2 + 1] += 40; add++;
										superWeight[temp1 - 2][temp2 + 2] += 40; add++;
		                          }
			                     }
			                  temp1--;
			                  temp2++;
			               } else myCount = 0;
		              } catch (ArrayIndexOutOfBoundsException e) {}
		           }
		        }
		     }

			      
		     
		     
		     if(add >= 2) return;
		     // 3(우대각/) 1공백 공격
			   for(int j = 0; j < 19; j++){
			      myCount = 0;
			      for (int i = 0;i<19;i++) {
			         int temp1 = i;
			         int temp2 = j;
			         for (int k = 0; k < 3; k++) {
			        	 try {
				             if (PlayBoard.playBoard[temp1][temp2] == Main.UserC) {
				               myCount++;
				                  if (myCount == 3) {
				                    if ((temp1 - 3 >= 0 && temp2 + 3 <= 18) && PlayBoard.playBoard[temp1 - 1][temp2+ 1] == 0 && PlayBoard.playBoard[temp1 - 2][temp2 + 2] == Main.UserC && PlayBoard.playBoard[temp1 - 3][temp2+ 3] == 0) {
				                    	superWeight[temp1 - 1][temp2+ 1] += 40; add++;
				                    	superWeight[temp1 - 3][temp2+ 3] += 40; add++;
				                    	
					                 }else if ((temp1 + 5 <= 18 && temp2 - 5 >= 0) && PlayBoard.playBoard[temp1 + 3][temp2- 3] == 0 && PlayBoard.playBoard[temp1 + 4][temp2- 4] == Main.UserC && PlayBoard.playBoard[temp1 + 5][temp2- 5] == 0) {
					                	superWeight[temp1 + 5][temp2 - 5] += 40; add++;
				                    	superWeight[temp1 + 3][temp2 - 3] += 40; add++;
					                 }
					               temp1--;
					               temp2++;
				                  } else myCount = 0;
				             }
			            } catch (ArrayIndexOutOfBoundsException e) {}
			           }
			         
			      }
			   }
			   
			    
			   if(add >= 2) return;
			   // 3(우대각/) 2공백 1 공격
			   for(int j = 0; j < 19; j++){
			      myCount = 0;
			      for (int i = 0;i<19;i++) {
			         int temp1 = i;
			         int temp2 = j;
			         for (int k = 0; k < 3; k++) {
			        	try {
				            if (PlayBoard.playBoard[temp1][temp2] == Main.UserC) {
				               myCount++;
				               if (myCount == 3) {
				                    if ((temp1 - 3 >= 0 && temp2 + 3 <= 18) && PlayBoard.playBoard[temp1 - 1][temp2+ 1] == 0 && PlayBoard.playBoard[temp1 - 2][temp2 + 2] == 0 && PlayBoard.playBoard[temp1 - 3][temp2+ 3] == Main.UserC) {
										superWeight[temp1 - 1][temp2+ 1] += 40; add++;
				                    	superWeight[temp1 - 2][temp2+ 2] += 40; add++;
				                    	
					                 }else if ((temp1 + 5 <= 18 && temp2 - 5 >= 0) && PlayBoard.playBoard[temp1 + 3][temp2- 3] == 0 && PlayBoard.playBoard[temp1 + 4][temp2- 4] == 0 && PlayBoard.playBoard[temp1 + 5][temp2- 5] == Main.UserC) {
					                	superWeight[temp1 + 4][temp2 - 4] += 40; add++;
				                    	superWeight[temp1 + 3][temp2 - 3] += 40; add++;
					                 }
					               temp1--;
					               temp2++;
				                  } else myCount = 0;
				            	}
			        	 }catch (ArrayIndexOutOfBoundsException e) {}
			         }
			      }
			   }
			   
			   
			   
			 
			  
			   if(add >= 2) return;
			      // 2 (공백2) 2 공격 (우대각/)오른쪽 위에서 왼쪽 아래 
			      for (int i = 0; i < 19; i++) {
			          myCount = 0;
			          for (int j = 0; j < 19; j++) {
			              int temp1 = i;
			              int temp2 = j;
			              for (int k = 0; k < 2; k++) {
			                 try {
			                      if (PlayBoard.playBoard[temp1][temp2] == Main.UserC) {
			                            myCount++;
			                            try {
			                               if (myCount == 2) {
			                                   //011011(up)
			                                   if ((temp1 + 5 <= 18 && temp1 - 5 >= 0) && PlayBoard.playBoard[temp1 + 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 + 5][temp2 - 5] == 0 && PlayBoard.playBoard[temp1 + 3][temp2 - 3] == Main.UserC && PlayBoard.playBoard[temp1 + 4][temp2 - 4] == Main.UserC) {
			                                       superWeight[temp1 + 2][temp2 - 2] += 40; add++;
			                                       superWeight[temp1 + 5][temp2 - 5] += 40; add++;
			                                    }
			                                   //101011(up)
			                                    else if ((temp1 + 5 <= 18 && temp1 - 5 >= 0) && PlayBoard.playBoard[temp1 + 4][temp2 - 4] == 0 && PlayBoard.playBoard[temp1 + 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 + 3][temp2 - 3] == Main.UserC && PlayBoard.playBoard[temp1 + 5][temp2 - 5] == Main.UserC) {
			                                          superWeight[temp1 + 2][temp2 - 2] += 40; add++;
			                                          superWeight[temp1 + 4][temp2 - 4] += 40; add++;
			                                    }
			                                   //110011(up)
			                                    else if ((temp1 + 5 <= 18 && temp1 - 5 >= 0) && PlayBoard.playBoard[temp1 + 3][temp2 - 3] == 0 && PlayBoard.playBoard[temp1 + 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 + 4][temp2 - 4] == Main.UserC && PlayBoard.playBoard[temp1 + 5][temp2 - 5] == Main.UserC) {
			                                          superWeight[temp1 + 2][temp2 - 2] += 40; add++;
			                                          superWeight[temp1 + 3][temp2 - 3] += 40; add++;
			                                    }
			                                   //101101(mid)
			                                    else if ((temp1 - 2 >= 0 && temp2 + 2 <= 18) && (temp1 + 3 <= 18 && temp2 - 3 >= 0) && PlayBoard.playBoard[temp1 + 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 - 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 - 2][temp2 + 2] == Main.UserC && PlayBoard.playBoard[temp1 + 3][temp2 - 3] == Main.UserC) {
			                                          superWeight[temp1 + 2][temp2 - 2] += 40; add++;
			                                          superWeight[temp1 - 1][temp2 + 1] += 40; add++;
			                                    }
			                                   //011011(mid)
				                            	   else if ((temp1 - 3 >= 0 && temp2 + 3 <= 18) && (temp1 + 2 <= 18 && temp2 - 2 >= 0) && PlayBoard.playBoard[temp1 - 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 + 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 - 2][temp2 + 2] == Main.UserC && PlayBoard.playBoard[temp1 - 3][temp2 + 3] == Main.UserC) {
				                                       superWeight[temp1 - 1][temp2 + 1] += 40; add++;
				                                       superWeight[temp1 + 2][temp2 - 2] += 40; add++;
				                               } 
			                                   //110110(mid)
				                            	   else if ((temp1 - 4 >= 0 && temp2 + 4 <= 18) && (temp1 + 1 <= 18 && temp2 - 1 >= 0) && PlayBoard.playBoard[temp1 - 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 + 2][temp2 - 2] == 0 && PlayBoard.playBoard[temp1 + 3][temp2 - 3] == Main.UserC && PlayBoard.playBoard[temp1 + 4][temp2 - 4] == Main.UserC) {
				                                       superWeight[temp1 - 1][temp2 + 1] += 40; add++;
				                                       superWeight[temp1 + 2][temp2 - 2] += 40; add++;
				                               }
			                                   //110011(down)
			                                    else if ((temp1 - 4 >= 0 && temp1 +4 <= 18) && PlayBoard.playBoard[temp1 - 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 - 2][temp2 + 2] == 0 && PlayBoard.playBoard[temp1 - 3][temp2 + 3] == Main.UserC && PlayBoard.playBoard[temp1 - 4][temp2 + 4] == Main.UserC) {
			                                          superWeight[temp1 - 1][temp2 + 1] += 40; add++;
			                                          superWeight[temp1 - 2][temp2 + 2] += 40; add++;
			                                    }
			                                   //110101(down)
			                                    else if ((temp1 - 4 >= 0 && temp1 +4 <= 18) && PlayBoard.playBoard[temp1 - 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 - 3][temp2 + 3] == 0 && PlayBoard.playBoard[temp1 - 2][temp2 + 2] == Main.UserC && PlayBoard.playBoard[temp1 - 4][temp2 + 4] == Main.UserC) {
			                                          superWeight[temp1 - 1][temp2 + 1] += 40; add++;
			                                          superWeight[temp1 - 3][temp2 + 3] += 40; add++;
			                                    }
			                                   //110110(down)
			                                    else if ((temp1 - 4 >= 0 && temp1 +4 <= 18) && PlayBoard.playBoard[temp1 - 1][temp2 + 1] == 0 && PlayBoard.playBoard[temp1 - 4][temp2 + 4] == 0 && PlayBoard.playBoard[temp1 - 3][temp2 + 3] == Main.UserC && PlayBoard.playBoard[temp1 - 2][temp2 + 2] == Main.UserC) {
			                                          superWeight[temp1 - 1][temp2 + 1] += 40; add++;
			                                          superWeight[temp1 - 4][temp2 + 4] += 40; add++;
			                                    }
			                               }
			                            } catch (ArrayIndexOutOfBoundsException e) {}
			                            temp1--;
			                            temp2++;
			                         } else myCount = 0;
			                 }
			                 catch(ArrayIndexOutOfBoundsException e) {}
			              }
			              }
			          }
			    
			    //한방오리백숙 ==========================================
					
			    //====================================================================

	   
	   
   }
	
   
}
