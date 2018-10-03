from setuptools import setup


def readme():
    with open('README.rst') as f:
        return f.read()


setup(name='ipcamera',
      version='0.0.1',
      description='Make USB camera an IP Camera',
      long_description=readme(),
      classifiers=[
        'Development Status :: 2 - Pre-Alpha',
        'Environment :: Console',
        'License :: OSI Approved :: MIT License',
        'Programming Language :: Python :: 3.7',
        'Topic :: Software Development :: Testing',
      ],
      keywords='first robotics frc roborio ipcamera usb mjpeg webcam',
      url='https://github.com/Team997Coders/BB2018BallFindingVision/tree/master/CameraServer',
      author='Chuck Benedict',
      author_email='chuck@benedict.email',
      license='MIT',
      packages=['ipcamera'],
      install_requires=[
      ],
      entry_points={
          'console_scripts': ['ipcamera=ipcamera.command_line:main'],
      },
      include_package_data=True,
      zip_safe=False)