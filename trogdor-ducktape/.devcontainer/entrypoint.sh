#!/bin/bash
# Remove base conda env from path
export PATH=${PATH/\/opt\/conda\/bin:/}
# Activate conda environment
. /opt/conda/etc/profile.d/conda.sh
conda activate trogdor-ducktape
# Run command
$@