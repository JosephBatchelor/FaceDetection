import org.opencv.core.*;
import org.opencv.core.Core;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.highgui.HighGui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import javax.swing.*;


public class VideoPanel extends JPanel{
    private BufferedImage image;

    public static VideoPanel videoPanel = new VideoPanel();
    public static JPanel test = new JPanel();
    public static JFrame videoFrame = new JFrame("Video");




    public static void main(String[] args){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        videoPanel.setVisible(true);

        test.setBackground(Color.red);
        test.setBounds(700, 200, 300 , 400);
        test.setVisible(true);


        //Create a JFrame to hold the video panel
        videoFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        videoFrame.setPreferredSize(new Dimension(1000, 800));
        videoFrame.add(test);
        videoFrame.add(videoPanel);
        videoFrame.pack();
        videoFrame.setVisible(true);

        //Live video detection

        VideoCapture camera = new VideoCapture(0);
        String xmlFile = "xml/lbpcascade_frontalface_improved.xml";
        CascadeClassifier cc = new CascadeClassifier(xmlFile);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    // Read a frame from the camera
                    Mat frame = new Mat();
                    camera.read(frame);

                    // If the frame was not read successfully, return
                    if (frame.empty()) {
                        return;
                    }

                    // Process the frame (e.g. detect faces)
                    MatOfRect faceDetection = new MatOfRect();
                    cc.detectMultiScale(frame, faceDetection);

                    int i = 1;
                    for (Rect rect : faceDetection.toArray()) {
                        Imgproc.rectangle(frame, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 0, 255), 2);
                        Imgproc.putText(frame, String.format("%d", i), new Point(rect.x + 40, rect.y - 20), Imgproc.FONT_HERSHEY_SIMPLEX, 1.0, new Scalar(51, 204, 51), 5);
                        i++;
                    }

                    // Update the UI
                    BufferedImage image = new BufferedImage(frame.width(), frame.height(), BufferedImage.TYPE_3BYTE_BGR);
                    image.getGraphics().drawImage(Mat2BufferedImage(frame), 0, 0, null);
                    videoPanel.updateImage(image);
                    videoFrame.repaint();

                    // Sleep for a short period of time
                    try {
                        Thread.sleep(33);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

// Create a Thread object and start the thread
        Thread thread = new Thread(runnable);
        thread.start();

    }

    public VideoPanel() {
        super();
    }

    public void updateImage(BufferedImage newImage) {
        image = newImage;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, 600, 400, null);
        }
    }



    public static void VideoCap(){

        VideoCapture camera = new VideoCapture(0);
        String xmlFile = "xml/lbpcascade_frontalface_improved.xml";
        CascadeClassifier cc = new CascadeClassifier(xmlFile);
        Mat frame = new Mat();

        //Checks to see if camera is working and that frames are being created.
        if (!camera.isOpened()){
            System.out.println("Error: Camera not open");
        }






        while (true){

            //while frames are being produced use classifier to find faces.
            if(camera.read(frame)){
                try {
                    //an array of rectangles
                    MatOfRect faceDetection = new MatOfRect();
                    cc.detectMultiScale(frame,faceDetection);

                    int i = 1;
                    for(Rect rect : faceDetection.toArray()){
                        Imgproc.rectangle(frame, new Point(rect.x, rect.y), new Point(rect.x + rect.width , rect.y+ rect.height), new Scalar(0,0,255), 2);
                        Imgproc.putText(frame, String.format("%d",i), new Point(rect.x+40, rect.y-20),Imgproc.FONT_HERSHEY_SIMPLEX, 1.0, new Scalar(51, 204, 51),5);
                        i++;
                    }


                    BufferedImage image = new BufferedImage(frame.width(), frame.height(), BufferedImage.TYPE_3BYTE_BGR);
                    image.getGraphics().drawImage(Mat2BufferedImage(frame), 0, 0, null);
                    videoPanel.updateImage(image);
                    videoFrame.repaint();


                    System.out.println(String.format("Faces Detected: %d", faceDetection.toArray().length));

                }catch (Exception e){
                    System.out.println("Async callback terminated");
                    camera.release();
                    videoFrame.dispose();
                    break;
                }
            } else {
                System.out.println("Error: could not read frame from camera");
                camera.release();
                videoFrame.dispose();
                break;
            }
        }
    }

    public static BufferedImage Mat2BufferedImage(Mat m) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (m.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels() * m.cols() * m.rows();
        byte[] b = new byte[bufferSize];
        m.get(0, 0, b); // get all the pixels
        BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }
}
