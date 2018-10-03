from setuptools import setup


def readme():
    with open('README.rst') as f:
        return f.read()


setup(name='ntserver',
      version='0.0.1',
      description='NetworkTables simulator server',
      long_description=readme(),
      classifiers=[
        'Development Status :: 2 - Pre-Alpha',
        'Environment :: Console',
        'License :: OSI Approved :: MIT License',
        'Programming Language :: Python :: 3.7',
        'Topic :: Software Development :: Testing',
      ],
      keywords='first robotics frc roborio networktables',
      url='https://github.com/Team997Coders/BB2018BallFindingVision/tree/master/NTSimulator',
      author='Chuck Benedict',
      author_email='chuck@benedict.email',
      license='MIT',
      packages=['ntserver'],
      install_requires=[
          'pynetworktables',
      ],
      entry_points={
          'console_scripts': ['ntserver=ntserver.command_line:main'],
      },
      include_package_data=True,
      zip_safe=False)