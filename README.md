# Networking Algorithms and Protocols Implementations

## Overview

This repository contains educational Java implementations and simulations of key networking algorithms and protocols related to computer networks and data communication. It is intended for students, learners, and enthusiasts looking to understand the core concepts behind routing, framing, congestion control, scheduling, and error detection in networks.

---

## Implementations Included

### 1. Routing Algorithms
- **Dijkstra’s Algorithm**: Computes shortest path in a graph (network) using greedy approach.
- **Distance Vector Routing**: Simulates routers exchanging routing tables and converging to stable routes dynamically.

### 2. Framing Schemes (Data Link Layer)
- **Bit Stuffing**: Inserts bits to avoid accidental frame delimiter occurrence (with destuffing).
- **Character Stuffing**: Uses special characters (DLE, STX, ETX) as frame delimiters and escapes them within data.

### 3. Error Detection
- **CRC (Cyclic Redundancy Check)**: Implements modular-2 division with any user-specified generator polynomial for framing and error checking.

### 4. Network Simulation Basics
- **Connecting hosts in a LAN**: Basic server-client Java example simulating LAN host connection over TCP sockets.

### 5. Congestion Control (TCP)
- **Slow Start**: Exponential growth of congestion window.
- **Congestion Avoidance**: Linear increase of congestion window after threshold.
- **Fast Retransmit & Fast Recovery**: Quick loss detection and recovery mechanisms using duplicate ACKs.

### 6. Packet Scheduling Algorithms
- **FIFO**: First in, first out queuing.
- **Priority Queue**: Packets scheduled based on priority.
- **Fair Queuing**: Round-robin over multiple flows.
- **Weighted Fair Queuing (WFQ)**: Each flow gets service proportional to assigned weight.

---

## How To Use

Each Java class contains a `main` method to run simple simulations or tests. You can:
- Run each file independently by compiling with `javac` and running with `java`.
- Modify the input data within the classes or add user input as required.
- Observe console output for step-by-step explanation or results.

---

## Learning Objectives

- Understand shortest path and dynamic routing principles via Dijkstra’s and Distance Vector algorithms.
- Comprehend framing techniques for error-free data transfer in data link layer.
- Learn error detection with CRC and generator polynomials.
- Grasp TCP congestion control mechanism with simulation of window dynamics.
- Explore packet scheduling strategies used in routers to manage network traffic.

---

## Tools & Environment

- Java 8+ for running the simulations.
- Console-based programs for interactive learning.

---

## References

- Networking textbooks and academic courses on Computer Networks.
- [GeeksforGeeks - Networking](https://www.geeksforgeeks.org/computer-networks/)
- RFC standards for TCP/IP and routing protocols.

---

## Contact

For suggestions or questions, please open an issue or reach out on the project repository.

---

This README provides a comprehensive overview of implementations for academic or practical learning use.

