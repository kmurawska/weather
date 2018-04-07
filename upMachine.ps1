$machineName = "weather"

$dockerMachines = docker-machine ls 
if ($dockerMachines -Like "*weather*") {
	echo "Starting docker machine..."
	docker-machine start $machineName
	docker-machine env $machineName
	&docker-machine env $machineName | Invoke-Expression
	echo "Docker machine started."
} else {
	echo "Creating docker machine..." 
	docker-machine create --driver virtualbox --virtualbox-disk-size "30000" --virtualbox-memory "4096" --virtualbox-cpu-count "2" --virtualbox-hostonly-cidr "192.168.90.1/24" $machineName 
	docker-machine start $machineName 
	docker-machine env $machineName
	&docker-machine env $machineName | Invoke-Expression
	echo "Docker machine created."
	
	echo "Fixing incorrect network adapter type..."
	docker-machine stop $machineName
	&"C:\Program Files\Oracle\VirtualBox\VBoxManage" modifyvm weather --nictype1 Am79C973
	echo "Network adapter type fixed."

    echo "Adding shared folders..."
    $weather = [System.IO.Path]::GetFullPath((Join-Path (pwd) '..\weather'))
    &"C:\Program Files\Oracle\VirtualBox\VBoxManage" sharedfolder add $machineName --name "/etc/weather" --hostpath "$weather" --automount
    echo "Shared folders added."
    echo ""

    docker-machine start $machineName

	echo "Setting ports forwarding on Oracle VirtualBox machine..."
	&"C:\Program Files\Oracle\VirtualBox\VBoxManage" controlvm "$machineName" natpf1 "payara,tcp,,8080,,8080"
	&"C:\Program Files\Oracle\VirtualBox\VBoxManage" controlvm "$machineName" natpf1 "payara admin console,tcp,,4848,,4848"
	&"C:\Program Files\Oracle\VirtualBox\VBoxManage" controlvm "$machineName" natpf1 "kafka 1,tcp,,9091,,9091"
	&"C:\Program Files\Oracle\VirtualBox\VBoxManage" controlvm "$machineName" natpf1 "kafka 2,tcp,,9092,,9092"
	&"C:\Program Files\Oracle\VirtualBox\VBoxManage" controlvm "$machineName" natpf1 "kafka 3,tcp,,9093,,9093"
	echo "Setting ports forwarding finished."

    docker-machine env $machineName
    &docker-machine env $machineName | Invoke-Expression
}