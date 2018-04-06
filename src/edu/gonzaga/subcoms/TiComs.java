package edu.gonzaga.subcoms;

import io.tlf.trevcom.TrevCom;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Scanner;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
//import java.lang.Float;
import javax.swing.JFrame;

/**
 *
 * @author Willis
 */
public class TiComs {

    public static volatile DataOutputStream output; //global variable output
    private static volatile FileInputStream missionFileIn;
    private static volatile FileOutputStream missionFileOut;
    private static volatile boolean MANUAL_MODE, AUTO_MODE;
    private static String input = "";
    private static char incomingByte;
    public static char EoT = '~';
    public static long start_time;
    public static String currentDate;
    private static byte D_HEADING = 0x00;
    private static byte D_DEPTH = 0x01;
    private static byte D_FORWARD = 0x02;
    private static float D_HEADING_VALUE;
    private static float D_DEPTH_VALUE;
    private static float D_FORWARD_VALUE;
    private static boolean HEADING_IS_SET;
    private static boolean DEPTH_IS_SET;
    private static boolean FORWARD_THRUST_IS_SET;

    public static JFrame frame;
    public static CompassPanel compass;
    public static BatteryPanel battery;
    public static GraphPanel psiGraph;
    public static GraphPanel depthGraph;
    public static ConsolePanel console;

    public static TrevCom COMS;

    private static volatile boolean inputFlag = true;

    public static void main(String[] args) throws InterruptedException {

        // Initializations
        initTimeStamp();
        initGUI();
        initCOMS();
        
        // Initialize file streaming with mission file
        //initMissionFileStreaming("mission.gen");

        // Initialize comms port with MCU
        int port = getPortNumber();
        setPort(port);

        // Successful communication establishment...
        System.out.println("[RASP]  Connected, linking data feed:\n");

        Thread t = new Thread() {       //establishes live read and write comms
            @Override
            public void run() {

                System.out.println("[RASP]  Starting in Manual Mode...");
                setManualMode();

                while (true) {

                    // Only forwards string commands to the MCU
                    if (MANUAL_MODE) {
                        inputFlag = false;
                        try {
                            readManualCommand();
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }

                    if (AUTO_MODE) {
                        runTestAutonomousMission();
                    }

                }
            }
        };
        t.start();
        while (true) {
            // Checks for data within the communication
            if (COMS.hasData()) {

                try {
                    char sentValue = COMS.read();
                    input += sentValue;

                    System.out.print(sentValue);

                    if (sentValue == EoT) {

                        if (input.contains("*&")) {
                            updateGUIgraphs();
                        } else {
                            getReturnHeader((byte)sentValue);
                            // Or we are recieving heartbeat
                        }

                        inputFlag = false;
                    }

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void runTestAutonomousMission(){
        
    }
    
    /* 
        Reads a command from console and forwards to MCU for handling
     */
    public static void readManualCommand() throws InterruptedException {

        if (!inputFlag) {
            Scanner scan = new Scanner(System.in);
            Thread.sleep(45); // Wait for MCU to respond
            System.out.print("\n[RASP]  Enter Commmand: ");
            String command = scan.nextLine();

            // If a command was entered, send it over the communication line
            command = "*" + command + "~";

            if (!command.equals("")) {

                try {
                    for (char c : command.toCharArray()) {
                        COMS.writeChar(c);
                    }

                } catch (IOException ex) {
                    System.err.println("Couldn't Send Command");
                    ex.printStackTrace();
                }
                // Convert the remaining characters into a float
                //float floatValue = Float.parseFloat(floatString) - (float) 0.00001;
                //byte[] b = ByteBuffer.allocate(4).putFloat(floatValue).array();

                // Send float value in 4 bytes
                inputFlag = true;
            }

        }

    }

    public static void writeCOMS(char c) {
        if (COMS == null) {
            System.err.println("COMS not initialized yet");
            return;
        }
        try {
            COMS.writeChar(c);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //for use with gui console
    public static String readConsole() {
        String inputString = console.getInput();
        while (inputString.equals("")) {
            inputString = console.getInput();
            try {
                Thread.sleep(0, 10);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        return inputString;
    }

    public static void scrollFix() {
        console.scrollDown();
    }

    public static void initTimeStamp() {
        start_time = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
        Date date = new Date();
        currentDate = dateFormat.format(date);
    }

    // All things to initialize the GUI go here
    public static void initGUI() {
        frame = new JFrame();
        FlowLayout flow = new FlowLayout();
        flow.setHgap(10);
        flow.setVgap(20);
        frame.setLayout(flow);
        compass = new CompassPanel(400, 400);

        battery = new BatteryPanel(100, 400, 16);
        psiGraph = new GraphPanel(500, 400, "Pressure PSI", "Time", "PSI");
        depthGraph = new GraphPanel(500, 400, "Depth PID Output", "Time", "Depth");
        //console = new ConsolePanel(700,200);

        frame.add(compass);
        frame.add(psiGraph);
        frame.add(depthGraph);
        frame.add(battery);
        //frame.add(console);
        frame.add(new DataPanel(500, 500));
        frame.setPreferredSize(new Dimension(1600, 700));
        frame.setVisible(true);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
    }

    // All things to printed when initializing communications go here
    public static void initCOMS() {

        System.out.println("[RASP]  Sub Communication " + currentDate);
        System.out.println("[RASP]  Opening Serial Communications...");
        COMS = new TrevCom(1, 115200);  // port Id, baud rate
        System.out.println("SELECT A COMS PORT:");
        for (int i = 0; i < COMS.getAvaliblePorts().length; i++) {  //searches for availble ports and prints them out
            System.out.println(i + ". " + COMS.getAvaliblePorts()[i]);
        }

    }

    // Get the port number for the MCU
    public static int getPortNumber() {
        int port = 1;   //sets port value temporarily to 1

        Scanner tmpIn = new Scanner(System.in);     //declares an inputString object (something fot the user to inputString) called tmpIn

        while (true) {
            boolean error = false;
            try {
                port = Integer.parseInt(tmpIn.nextLine());      //gets user inputString to decide which port to use
                //port = Integer.parseInt(readConsole());
                if (port < 0 || port >= COMS.getAvaliblePorts().length) {
                    error = true;
                }
            } catch (Exception e) {
                error = true;
            }
            if (error) {
                System.out.println("[RASP]  That is not a valid port number!");
            } else {
                break;
            }
        }
        return port;
    }
    // Set the communication port with the MCU 

    public static void setPort(int port) {
        System.out.println("[RASP]  Connecting Raspberry Pi communications with MCU...");

        COMS.setPort(port);         // sets real port value
        if (!COMS.connect()) {
            System.out.println("[RASP]  Unable to connect!");
            return;
        }
    }

    public static void updateGUIgraphs() {
        System.out.println();
        int receiveFloatIndex;
        String receiveFloatString;
        receiveFloatIndex = input.indexOf("*&");
        receiveFloatString = input.substring(receiveFloatIndex - 5, receiveFloatIndex);

        char[] floatBytes = {(char) receiveFloatString.charAt(1), (char) receiveFloatString.charAt(2), (char) receiveFloatString.charAt(3), (char) receiveFloatString.charAt(4)};

        float receiveFloatValue = Float.intBitsToFloat(floatBytes[3] | (floatBytes[2] << 8 | floatBytes[1] << 16 | (floatBytes[0] << 24)));

        String identifier;

        switch (receiveFloatString.substring(0, 1).charAt(0)) {
            case 'a':
                identifier = "PRESSURE_RESISTOR";
                break;
            case 'b':
                identifier = "PRESSURE_ADC";
                break;
            case 'c':
                identifier = "PRESSURE_PSI";
                break;
            case 'd':
                identifier = "DPRESSURE_PSI";
                break;
            case 'e':
                identifier = "DEPTH_PID_OUTPUT";
                break;
            case 'f':
                identifier = "DEPTH_P";
                break;
            case 'g':
                identifier = "DEPTH_I";
                break;
            case 'h':
                identifier = "DEPTH_D";
                break;
            case 'j':
                identifier = "GYRO_ANGLE";
                break;
            case 'k':
                identifier = "DHEADING";
                break;
            default:
                identifier = receiveFloatString.substring(0, 1);
        }

        System.out.printf("[MCU]%17s: %8d, %10f\n", identifier, (System.currentTimeMillis() - start_time), receiveFloatValue);
        if (identifier.equals("PRESSURE_PSI")) {
            if (!psiGraph.hasStarted()) {
                psiGraph.setStartTime(System.currentTimeMillis() - start_time);
            }
            psiGraph.addPoint((System.currentTimeMillis() - start_time), receiveFloatValue);

        }

        if (identifier.equals("GYRO_ANGLE")) {
            compass.setAngle(receiveFloatValue);
        }
        if (identifier.equals("DEPTH_PID_OUTPUT")) {
            if (!depthGraph.hasStarted()) {
                depthGraph.setStartTime(System.currentTimeMillis() - start_time);
            }
            depthGraph.addPoint((System.currentTimeMillis() - start_time), receiveFloatValue);
        }

        input = "";
    }

    public static void setAutoMode() {
        AUTO_MODE = true;
        MANUAL_MODE = false;
    }

    public static void setManualMode() {
        AUTO_MODE = false;
        MANUAL_MODE = true;
    }

    private static void initMissionFileStreaming(String missionFile) {
        try {
            missionFileIn = new FileInputStream("mission.gen");
            missionFileOut = new FileOutputStream("mission.gen");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
    private static void getReturnHeader(byte header){
        
        switch(header){
            case 0x00:
                HEADING_IS_SET = true;
            break;
            case 0x01:
                DEPTH_IS_SET = true;
            break;
            case 0x02:
                FORWARD_THRUST_IS_SET = true;
            break;
            default:
                System.err.println("Received unknown return header from MCU");
        }
    }
    
}
