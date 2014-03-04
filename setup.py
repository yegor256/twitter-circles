from setuptools import setup
from setuptools.command.test import test
import sys

class CirclesTest(test):
    def finalize_options(self):
        test.finalize_options(self)
        self.test_args = []
        self.test_suite = True
    def run_tests(self):
        import pytest
        import tox
        if (pytest.main(self.test_args)):
            sys.exit(-1)
        if (tox.cmdline(self.test_args)):
            sys.exit(-1)

setup(
    name = 'circles',
    version = '0.1',
    url = 'http://github.com/curiost/twitter-circles',
    license = 'Apache Software License',
    author = 'Yegor Bugayenko',
    tests_require = ['pytest', 'tox'],
    cmdclass = {'test': CirclesTest},
    author_email = 'yegor@tpc2.com',
    description = 'Twitter Circles',
    long_description = '',
    packages = ['circles'],
    include_package_data = True,
    platforms = 'any',
    test_suite = 'circles.test.test_Circle',
    classifiers = [
        'Programming Language :: Python',
        'Development Status :: 0 - Beta',
        'Natural Language :: English',
        'Environment :: Web Environment',
        'Intended Audience :: Developers',
        'License :: OSI Approved :: Apache Software License',
        'Operating System :: OS Independent',
        'Topic :: Software Development :: Libraries :: Python Modules',
        'Topic :: Software Development :: Libraries :: Application Frameworks',
        'Topic :: Internet :: WWW/HTTP :: Dynamic Content',
    ],
    scripts = ['scripts/circles.py'],
    extras_require = {
        'testing': ['pytest'],
    }
)