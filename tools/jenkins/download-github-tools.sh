if [ ! -f /tmp/github-tools-v1.0.0-linux-x64.tar.gz ]
then
    echo 'Downloading and unzipping github-tools files...'
    wget -P /tmp -nc https://github.com/bcgov/EDUC-INFRA-COMMON/raw/master/github-tools/dist/github-tools-v1.0.0/github-tools-v1.0.0-linux-x64.tar.gz
    cd /tmp
    tar zxvf github-tools-v1.0.0-linux-x64.tar.gz
fi