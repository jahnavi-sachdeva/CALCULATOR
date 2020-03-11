package matrix;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class Calculator extends JFrame{
	JPanel panTop,panBottom,panMemory;
	JTextField txtHistory,txtCurrent;
	JButton btnMemory[],btnOthers[];
	Font fnt;
	boolean overlapFlag=false;
	double history,memory,oldHistory;
	char currentOp='\u0000',oldOp='\u0000';
	String strMemory[]= {"MC","MR","M+","M-","MS"};
	String strOthers[]=  {"%","\u221A","x\u00B2"," 1/x","CE","C","\u232b","\u00F7","7","8","9","\u00D7","4","5","6","-","1","2","3","+","\u2213","0",".","="};
	Calculator(){
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	//panel top
		//TextField History
		txtHistory=new JTextField("");
		txtHistory.setHorizontalAlignment(SwingConstants.RIGHT);
		txtHistory.setEditable(false);
		fnt=new Font(Font.SERIF,Font.BOLD,30);
		txtHistory.setFont(fnt);

		//TextField Current
		txtCurrent=new JTextField("0");
		txtCurrent.setHorizontalAlignment(SwingConstants.RIGHT);
		txtCurrent.setEditable(false);
		fnt=new Font(Font.SERIF,Font.BOLD,40);
		txtCurrent.setFont(fnt);
		
		//Button Memory
		panMemory=new JPanel();
		panMemory.setLayout(new GridLayout(1,5));
		btnMemory=new JButton[5];
		for(int i=0;i<btnMemory.length;i++) {
			btnMemory[i]=new JButton(strMemory[i]);
			btnMemory[i].setBackground(new Color(220,220,220));
			btnMemory[i].setFocusable(false);
			fnt=new Font(Font.SERIF,Font.BOLD,20);
			btnMemory[i].setFont(fnt);
			btnMemory[i].setBorderPainted(false);
			btnMemory[i].addMouseListener(new MouseAdapter() {
				public void mouseEntered(MouseEvent me) {
					JButton btn=(JButton)me.getSource();
					btn.setBackground(new Color(200,200,200));
				}
				public void mouseExited(MouseEvent me) {
					JButton btn=(JButton)me.getSource();
					btn.setBackground(new Color(220,220,220));
				}
			});
			btnMemory[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					JButton btn=(JButton)ae.getSource();
					String lbl=btn.getText();
					String currentStr=txtCurrent.getText();
					double c=Double.parseDouble(currentStr);
					if(lbl.equalsIgnoreCase("mc")) {
						memory=0;
						overlapFlag=true;
					}
					else if(lbl.equalsIgnoreCase("mr")) {
						txtCurrent.setText(val(memory+""));
						overlapFlag=true;
					}
					else if(lbl.equalsIgnoreCase("m+")) {
						memory+=c;
						overlapFlag=true;
					}
					else if(lbl.equalsIgnoreCase("m-")) {
						memory-=c;
						overlapFlag=true;
					}
					else if(lbl.equalsIgnoreCase("ms")) {
						memory=c;
						overlapFlag=true;
					}
				}
			});
			panMemory.add(btnMemory[i]);
		}
		
		panTop=new JPanel();
		panTop.setLayout(new GridLayout(3,1));
		panTop.add(txtHistory);
		panTop.add(txtCurrent);
		panTop.add(panMemory);
		
		add(panTop,BorderLayout.NORTH);
		
	//Panel Bottom
		//Button others
		panBottom=new JPanel();
		panBottom.setLayout(new GridLayout(6,4,5,5));
		
		btnOthers=new JButton[24];
		for(int i=0;i<24;i++) {
			btnOthers[i]=new JButton(strOthers[i]);
			if(Character.isDigit(strOthers[i].charAt(0)))
				btnOthers[i].setBackground(new Color(250,250,250));
			else
				btnOthers[i].setBackground(new Color(240,240,240));
			btnOthers[i].setFocusable(false);
			fnt=new Font(Font.SERIF,Font.BOLD,22);
			btnOthers[i].setFont(fnt);
			btnOthers[i].setBorderPainted(false);
			btnOthers[i].addMouseListener(new MouseAdapter() {
				public void mouseEntered(MouseEvent me) {
					JButton btn=(JButton)me.getSource();
					if(isOperator(btn.getText().charAt(0)) || btn.getText().charAt(0)=='=') {
						btn.setBackground(new Color(0,0,255));
						btn.setForeground(new Color(255,255,255));
					}
					else
						btn.setBackground(new Color(220,220,220));
				}
				public void mouseExited(MouseEvent me) {
					JButton btn=(JButton)me.getSource();
					if(Character.isDigit(btn.getText().charAt(0)))
						btn.setBackground(new Color(250,250,250));
					else
						btn.setBackground(new Color(240,240,240));
					btn.setForeground(new Color(0,0,0));
				}
			});
			btnOthers[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					JButton btn=(JButton)ae.getSource();
					String lbl=btn.getText();
					String currentStr=txtCurrent.getText();
					String historyStr=txtHistory.getText();
					double c;
					try {
						c=Double.parseDouble(currentStr);
					}
					catch(NumberFormatException e) {
						c=0;
					}
					if(Character.isDigit(lbl.charAt(0))) {
						if(overlapFlag==false)
							txtCurrent.setText(val(currentStr+lbl.charAt(0)));
						else {
							txtCurrent.setText(val(lbl));
							overlapFlag=false;
						}
					}
					else if(isOperator(lbl.charAt(0)) ){
						if(overlapFlag==true) {
							currentOp=lbl.charAt(0);
							txtHistory.setText(historyStr.substring(0,historyStr.length()-1)+currentOp);
							return;
						}
						if(currentOp!='\u0000') {
							double ans=solve(currentOp,history,c);
							history=ans;
							txtCurrent.setText(val(ans+""));
						}
						else {
							history=c;							
						}
						overlapFlag=true;
						currentOp=lbl.charAt(0);
						txtHistory.setText(historyStr+currentStr+currentOp);
					}
					else if(lbl.equalsIgnoreCase("=")) {
						if(currentOp!='\u0000') {
							double ans=solve(currentOp,history,c);
							txtCurrent.setText(val(ans+""));
							oldHistory=c;
							oldOp=currentOp;
							history=0;
							currentOp='\u0000';
							txtHistory.setText("");
							overlapFlag=false;
						}
						else {
							if(oldOp!='\u0000') {
								double ans=solve(oldOp,c,oldHistory);
								txtCurrent.setText(val(ans+""));
							}
						}
					}
					else if(lbl.equalsIgnoreCase("%")) {
						double ans=history*c/100;
						txtCurrent.setText(val(ans+""));
						txtHistory.setText(historyStr+val(ans+""));
						overlapFlag=true;
					}
					else if(lbl.equalsIgnoreCase("\u221A")) {//sqrt
						txtCurrent.setText(val(Math.sqrt(c)+""));
						overlapFlag=true;
					}
					else if(lbl.equalsIgnoreCase("x\u00B2")) {//sqr
						txtCurrent.setText(val(c*c+""));
						overlapFlag=true;
					}					
					else if(lbl.equalsIgnoreCase(" 1/x")) {
						if(c==0)
							txtCurrent.setText("Can't Divide by zero");
						else
							txtCurrent.setText(val(1/c+""));
						overlapFlag=true;
					}					
					else if(lbl.equalsIgnoreCase("CE")) {
						txtCurrent.setText("0");
						overlapFlag=true;
					}					
					else if(lbl.equalsIgnoreCase("C")) {
						txtCurrent.setText("0");
						txtHistory.setText("");
						history=0;
						currentOp='\u0000';
						overlapFlag=true;
					}					
					else if(lbl.equalsIgnoreCase("\u232b")) {//backspace
						txtCurrent.setText(val(currentStr.substring(0,currentStr.length()-1)));
					}														
					else if(lbl.equalsIgnoreCase("\u2213")) {//sign
						txtCurrent.setText(val(c*-1+""));
					}					
					else if(lbl.equalsIgnoreCase(".")) {
						if(currentStr.indexOf(".")==-1)
							txtCurrent.setText(currentStr+".");
					}					
				}
			});
			panBottom.add(btnOthers[i]);
		}
		add(panBottom,BorderLayout.CENTER);
		
		setSize(400,700);
		setVisible(true);
	}
	boolean isOperator(char ch) {
		if(ch=='+' || ch=='-' ||ch=='\u00D7' || ch=='\u00F7')
			return true;
		else
			return false;
	}
	public static void main(String[] args) {
		new Calculator();
	}
	String val(String str) {
		if(str.equals(""))
			return "0";
		else {
			double t=Double.parseDouble(str);
			if(t==(int)t) 
				return (int)t+"";
			else
				return t+"";
		}
	}
	double solve(char op,double op1,double op2) {
		switch(op) {
			case '+':return op1+op2;
			case '-':return op1-op2;
			case '\u00D7':return op1*op2;
			case '\u00F7':return op1/op2;
			default:return 0;
		}
	}
}
