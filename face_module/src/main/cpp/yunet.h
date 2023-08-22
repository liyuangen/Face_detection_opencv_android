
#ifndef YUNET_H
#define YUNET_H

#include <string>
#include <opencv2/opencv.hpp>

using namespace cv;
using namespace std;
class YuNet {
public:
    YuNet(const std::string &model_path,
          const cv::Size &input_size = cv::Size(320, 320),
          float conf_threshold = 0.6f,
          float nms_threshold = 0.3f,
          int top_k = 5000,
          int backend_id = 0,
          int target_id = 0)
            : model_path_(model_path), input_size_(input_size),
              conf_threshold_(conf_threshold), nms_threshold_(nms_threshold),
              top_k_(top_k), backend_id_(backend_id), target_id_(target_id) {
        model = cv::FaceDetectorYN::create(model_path_, "", input_size_, conf_threshold_,
                                           nms_threshold_, top_k_, backend_id_, target_id_);
    }

    void setBackendAndTarget(int backend_id, int target_id) {
        backend_id_ = backend_id;
        target_id_ = target_id;
        model = cv::FaceDetectorYN::create(model_path_, "", input_size_, conf_threshold_,
                                           nms_threshold_, top_k_, backend_id_, target_id_);
    }

    /* Overwrite the input size when creating the model. Size format: [Width, Height].
    */
    void setInputSize(const cv::Size &input_size) {
        input_size_ = input_size;
        model->setInputSize(input_size_);
    }

    cv::Mat infer(const cv::Mat image) {
        cv::Mat res;
        model->detect(image, res);
        return res;
    }

private:
    cv::Ptr<cv::FaceDetectorYN> model;

    std::string model_path_;
    cv::Size input_size_;
    float conf_threshold_;
    float nms_threshold_;
    int top_k_;
    int backend_id_;
    int target_id_;
};
#endif //YUNET_H