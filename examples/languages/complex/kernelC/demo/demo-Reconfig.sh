#echo -en "\033[1;34m"
#echo "First, we need to compile the definition of KernelC"
#echo -en "\033[0m"
#echo -en "\033[1;34m"
#echo "This might take a while..."
#echo "kompile.pl kernelc"
#echo -en "\033[0m"
#kompile.pl kernelc
#cp kernelc-compiled.maude kernelc-compiled-demo.maude
#echo -en "\033[1;34m"
#echo -n "Done. Now let's compile pReconfig" 
#echo -en "\033[0m"
#read
echo -en "\033[1;34m"
echo "kcompile-program.sh pReconfig.k KERNELC  KERNELC-RECONFIG pReconfig"
echo -en "\033[0m"
cp pReconfig.c pReconfig.k
kcompile-program.sh pReconfig.k KERNELC  KERNELC-RECONFIG pReconfig
echo -en "\033[1;34m"
echo "Done. Now, let's execute it"
echo -en "\033[0m"
echo -en "\033[1;34m"
echo -n "in Maude:  rewrite run('pReconfig) ."
echo -en "\033[0m"
read
krunf.sh runReconfig.maude
