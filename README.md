# Computer Networks Lab - Algorithm Implementations

This repository contains Java implementations for standard Computer Networks laboratory experiments. The codes cover the Data Link Layer, Network Layer, and Transport Layer protocols using Socket Programming and Simulation logic.

---

## 📋 Table of Contents

- [Prerequisites](#prerequisites)
- [How to Run Socket Programs](#how-to-run-socket-programs)
- [Part A: Data Link Layer (Framing & Error Control)](#part-a-data-link-layer-framing--error-control)
- [Part B: Flow Control (Sliding Window Protocols)](#part-b-flow-control-sliding-window-protocols)
- [Part C: Network Layer (Routing & Switching)](#part-c-network-layer-routing--switching)
- [Part D: Transport Layer (TCP & Scheduling)](#part-d-transport-layer-tcp--scheduling)

---

## 🛠 Prerequisites

- **Java Development Kit (JDK)**: Version 8 or higher.

---

## 🚀 How to Run Socket Programs

Most Data Link Layer experiments use Client-Server Architecture. You must run them in the following order:

1. Compile both files:
```javac Sender.java Receiver.java```

2. Run the Receiver (Server) first:
```java Receiver```

It will pause and wait for a connection.

3. Run the Sender (Client) in a new terminal:
```java Sender```


**Tip:** If you get a `BindException: Address already in use` error, wait 10 seconds for the port to clear or change the port number (e.g., 9999 to 8080) in both source files.

---

## 📡 Part A: Data Link Layer

### 1. Checksum (Error Detection)

- **Files:** ChecksumSender.java, ChecksumReceiver.java
- **Logic:** Sender calculates Sum & 1's Complement. Receiver adds Sum + Checksum. Result must be all 1s (-1).
- Includes random 50% or 30% chance to corrupt data to demonstrate error detection.

### 2. CRC (Cyclic Redundancy Check)

- **Files:** CRCSender.java, CRCReceiver.java
- **Logic:** Uses Modulo-2 Binary Division (XOR).
- **Input:** User enters Binary Data (e.g., 10110) and Generator Polynomial (e.g., 1101).
- Simulates bit flipping to prove the receiver rejects corrupted frames.

### 3. Framing Algorithms (Bit & Character Stuffing)

- **File:** FramingSchemes.java
- **Type:** Menu-Driven Console Application.
- Bit Stuffing: Inserts a 0 after five consecutive 1s.
- Character Stuffing: Escapes `DLE` characters in data by doubling them (`DLEDLE`).

---

## 🔄 Part B: Flow Control (Sliding Window)

### 4. Stop and Wait ARQ

- **Files:** Sender.java, Receiver.java
- **Logic:** Sender sends 1 packet and waits. Uses `socket.setSoTimeout(2000)` to detect lost packets.
- Covers Lost Frame, Lost ACK, and Delayed ACK scenarios.

### 5. Go-Back-N ARQ

- **Files:** GBNSender.java, GBNReceiver.java
- Sender has a window size (e.g., 4). On Timeout, sender resends the entire window from last un-acked packet.
- Receiver discards any out-of-order packet (Cumulative ACK).

### 6. Selective Repeat ARQ

- **Files:** SRSender.java, SRReceiver.java
- Sender resends only the specific lost packet.
- Receiver accepts out-of-order packets and buffers them. Sends individual ACKs.

---

## 🌐 Part C: Network Layer

### 7. Dijkstra's Algorithm (Shortest Path)

- **File:** Dijkstra.java
- **Input:** Adjacency Matrix (Weights).
- **Output:** Prints shortest cost and exact path (e.g., 0 → 2 → 1 → 4) from source to all nodes.

### 8. Distance Vector Routing (DVR)

- **File:** DistanceVectorRouting.java
- Simulates Bellman-Ford equation. Routers exchange tables iteratively until convergence.
- Prints final Routing Table (Destination | Cost | Next Hop).

### 9. Connecting Hosts in LAN (Learning Switch)

- **File:** LANSwitch.java
- Simulates a Switch Forwarding Table with Learning (records Source MAC & Port), Flooding, and Forwarding phases.

---

## 📦 Part D: Transport Layer

### 10. Packet Scheduling Algorithms

- **File:** PacketScheduling.java
- **Type:** Menu-Driven Console Application.
- Includes FIFO, Priority Queue, Fair Queuing (Round-Robin), and Weighted Fair Queuing (WFQ).

### 11. TCP Congestion Control

- **File:** TCPCongestionControl.java
- **Type:** State Machine Simulation.
- Includes Slow Start (Exponential), Congestion Avoidance (Linear), Fast Retransmit, and Fast Recovery.
- User inputs events (a for ACK, t for Timeout, d for 3 Duplicate ACKs) to see `cwnd` change.

---




