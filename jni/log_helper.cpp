#include "log_helper.hpp"

double tdiff(timespec time1, timespec time2) {
  return time2.tv_sec + (double)time2.tv_nsec / 1e9 -  time1.tv_sec - (double)time1.tv_nsec / 1e9;
}
