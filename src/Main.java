import org.opencv.core.*;
import org.opencv.core.Core;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.highgui.HighGui;


public class Main {

    public static void main(String[] args){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        FaceDetectionPhoto();
          VideoCap();
        System.exit(0);
    }


    public static void VideoCap(){
        VideoCapture camera = new VideoCapture(0);
        String xmlFile = "xml/lbpcascade_frontalface_improved.xml";
        CascadeClassifier cc = new CascadeClassifier(xmlFile);

        //Checks to see if camera is working and that frames are being created.
        if (!camera.isOpened()){
                System.out.println("Error: Camera not open");
        }
        //While the camera is open and is producing frames.
        while (true){
            //
            Mat frame = new Mat();

            if(camera.read(frame)){
                try {

                    MatOfRect faceDetection = new MatOfRect();
                    cc.detectMultiScale(frame,faceDetection);

                    int i = 1;
                    for(Rect rect : faceDetection.toArray()){
                        Imgproc.rectangle(frame, new Point(rect.x, rect.y), new Point(rect.x + rect.width , rect.y+ rect.height), new Scalar(0,0,255), 2);
                        Imgproc.putText(frame, String.format("%d",i), new Point(rect.x+200, rect.y+100),Imgproc.FONT_HERSHEY_SIMPLEX, 1.0, new Scalar(51, 204, 51),5);
                        i++;
                    }

                    HighGui.namedWindow("Video", HighGui.WINDOW_AUTOSIZE);
                    HighGui.imshow("Video", frame);
                    int delay = 1000 / 60; // 60 fps
                    HighGui.waitKey(delay);

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
                System.out.println("Error: could not read frame from camera");
                camera.release();
                HighGui.destroyAllWindows();
                break;
            }
        }
    }


    public static void FaceDetectionPhoto(){
        Mat src = Imgcodecs.imread("images/test2.png");
        String xmlFile = "xml/lbpcascade_frontalface.xml";

        CascadeClassifier cc = new CascadeClassifier(xmlFile);
        MatOfRect faceDetection = new MatOfRect();
        cc.detectMultiScale(src, faceDetection);
        int i = 0;
        for(Rect rect : faceDetection.toArray()){

            Imgproc.rectangle(src, new Point(rect.x, rect.y), new Point(rect.x + rect.width , rect.y+ rect.height), new Scalar(0,0,255), 2);
            Imgproc.putText(src, String.format("person: %d",i), new Point(rect.x, rect.y),Imgproc.FONT_HERSHEY_SIMPLEX, 1.0, new Scalar(0, 0, 0),3);
            i++;
        }

        Imgcodecs.imwrite("images/test2_out.png", src);

        System.out.println(String.format("Detected faces: %d", faceDetection.toArray().length));
        System.out.println("Image Detection Finished");
    }

}
