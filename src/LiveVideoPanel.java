import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

public class LiveVideoPanel extends JPanel  {
    private BufferedImage image;

    public LiveVideoPanel() {
        // Create a new JFrame to hold the panel
        JFrame jframe = new JFrame("Live Video");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.pack();
        jframe.setVisible(true);

        VideoCapture camera = new VideoCapture(0);
        String xmlFile = "xml/lbpcascade_frontalface_improved.xml";
        CascadeClassifier cc = new CascadeClassifier(xmlFile);
        Mat frame = new Mat();

        if (!camera.isOpened()){
            System.out.println("Error: Camera not open");
        }
        //While the camera is open and is producing frames.
        while (true){

            //while frames are being produced use classifier to find faces.
            if(camera.read(frame)){
                try {
                    //an array of rectangles
                    MatOfRect faceDetection = new MatOfRect();
                    cc.detectMultiScale(frame,faceDetection);

                    int i = 1;
                    for(Rect rect : faceDetection.toArray()){
                        Imgproc.rectangle(frame, new org.opencv.core.Point(rect.x, rect.y), new org.opencv.core.Point(rect.x + rect.width , rect.y+ rect.height), new Scalar(0,0,255), 2);
                        Imgproc.putText(frame, String.format("%d",i), new Point(rect.x+200, rect.y+100),Imgproc.FONT_HERSHEY_SIMPLEX, 1.0, new Scalar(51, 204, 51),5);
                        i++;
                    }

                    image = Utils.matToBufferedImage(frame);
                    repaint();

                    System.out.println(String.format("Faces Detected: %d", faceDetection.toArray().length));

                    if(HighGui.n_closed_windows == 1){
                        camera.release();
                        HighGui.destroyAllWindows();
                        break;
                    }
                }catch (Exception e){
                    System.out.println("Async callback terminated");
                }
            } else {
                System.out.println("Error: could not read jframe from camera");
                camera.release();
                HighGui.destroyAllWindows();
                break;
            }
        }

    }

    public static class Utils {
        public static BufferedImage matToBufferedImage(Mat mat) {
            int type = BufferedImage.TYPE_BYTE_GRAY;
            if (mat.channels() > 1) {
                type = BufferedImage.TYPE_3BYTE_BGR;
            }
            int bufferSize = mat.channels() * mat.cols() * mat.rows();
            byte[] buffer = new byte[bufferSize];
            mat.get(0, 0, buffer); // get all the pixels
            BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
            final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
            return image;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the image onto the panel
        g.drawImage(image, 0, 0, this);
    }
}
