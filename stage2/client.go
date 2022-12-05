package main

import (
	"bufio"
	"fmt"
	"net"
	"strings"
)

func main() {
	c, err := net.Dial("tcp", "127.0.0.1:1234")
	if err != nil {
		fmt.Println(err)
		return
	}
	fmt.Println("Client started!")

	text := "Give me everything you have!"
	fmt.Fprintf(c, text+"\n")
	fmt.Println("Sent:", text)

	message, _ := bufio.NewReader(c).ReadString('\n')
	fmt.Print("Received: ", message)
	if strings.TrimSpace(text) == "STOP" {
		fmt.Println("TCP client exiting...")
		return
	}

	c.Write([]byte("All files were sent!"))
}
