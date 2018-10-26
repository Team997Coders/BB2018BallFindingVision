using DirectShowLib;
using System.Linq;

// On Windows, do a `dotnet run` on this project to turn off auto-focus on a LifeCam 5000 USB camera.
// The LifeCam software does not work on Windows 10 Creators edition
// and so therefore one cannot easily turn off auto-focus.
// This could be made more robust by accepting or listing available cameras.
// It also could provide a pause such that one could look at the state of focus
// of the camera and then hit a key to focus.

// Started with the code posted here: https://stackoverflow.com/a/18189027/206
// Pulled from https://github.com/anotherlab/FocusUF/blob/master/FocusUF/Program.cs

namespace FocusUF
{
    class Program
    {
        static void Main(string[] args)
        {
            // Get the list of connected video cameras
            DsDevice[] devs = DsDevice.GetDevicesOfCat(FilterCategory.VideoInputDevice);
            
            // Filter that list down to the one with hyper-aggressive focus
            var dev = devs.Where(d => d.Name.Equals("Microsoft® LifeCam HD-5000")).FirstOrDefault();

            if (dev != null)
            {
                // DirectShow uses a module system called filters to exposure the functionality
                // We create a new object that implements the IFilterGraph2 interface so that we can
                // new filters to exposure the functionality that we need.
                if (new FilterGraph() is IFilterGraph2 graphBuilder)
                {
                    // Create a video capture filter for the device
                    graphBuilder.AddSourceFilterForMoniker(dev.Mon, null, dev.Name, out IBaseFilter capFilter);

                    // Cast that filter to IAMCameraControl from the DirectShowLib
                    IAMCameraControl _camera = capFilter as IAMCameraControl;

                    // Get the current focus settings from the webcam
                    _camera.Get(CameraControlProperty.Focus, out int v, out CameraControlFlags f);

                    // If the camera was not in manual focus mode, lock it into manual at the current focus setting
                    if (f != CameraControlFlags.Manual)
                    {
                        _camera.Set(CameraControlProperty.Focus, v, CameraControlFlags.Manual);
                    }
//                    _camera.Set(CameraControlProperty.Exposure, 20, CameraControlFlags.Auto);
                }
            }
        }
    }
}