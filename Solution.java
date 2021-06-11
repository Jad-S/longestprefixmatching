/**
 * This class uses the Longest Prefix Match algorithm, which determines where to forward a packet
 * once it has arrived to a router. It simulates this packet-forwarding behaviour and returns
 * the interfaces used for each IP address, which corresponds to their appearance in the IPAddresses input file.
 *  (e.g. First outputted interfaces matches first ip address, etc..)
 *
 * @author: Jad Bou Said
 * @ID_Number: B00756169
 */

import java.util.*;
import java.io.*;
public class Solution {
    //This is a basic Route class, which consists of three parameters:
    //Network ID, Subnet Mask and Next-Hop Interface.
    public static class Route {
        String networkID;
        String subnet_Mask;
        int nHop_Interface;

        public Route(String netID, String subMask, int nHop) {
            this.networkID = netID;
            this.subnet_Mask = subMask;
            this.nHop_Interface = nHop;
        }

        public String toString() {
            return networkID + "    " + subnet_Mask + "    " + nHop_Interface;
        }
    }

    public static void main(String[]args){

        //retrieving and storing data from "DestinationIPAddresses.txt"
        ArrayList<String> IP_Addresses = new ArrayList<>(); // arraylist of ip addresses
        File destination = new File ("DestinationIPAddresses.txt");
        try {
            Scanner sc = new Scanner(destination);
            while (sc.hasNext()) {
                IP_Addresses.add(sc.nextLine()); // add each line to the list of ip addresses
            }
        } catch (IOException exception) {
            System.out.println("IP Addresses file not found.");
        }

        //retrieving and storing data from "RoutingTable.txt"
        ArrayList<Route> RoutingTable = new ArrayList<>();
        File route = new File("RoutingTable.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader("RoutingTable.txt"));
            String line;

            Route r;
            // read each line as different route
            // then, dynamically add new route object to list of routes using route "r"
            while ((line = br.readLine()) != null) {
                String [] temp = line.split(" +"); //each token is split by more than one space
                String netID = temp [0]; // net ID value
                String subMask = temp [1]; // subnet mask value
                int nHop = Integer.parseInt(temp [2]); // Next hop interface value
                r = new Route(netID, subMask, nHop); // initialize route "r" object
                RoutingTable.add(r); // add route object to the arraylist list of routes
            }
        } catch (IOException exception) {
            System.out.println("Routing Table file not found.");
        }

        //print all destination IP addresses
        System.out.println("The destination IP addresses are:");
        for (String ip_address : IP_Addresses) {
            System.out.println(ip_address);
        }

        //print routing table contents
        System.out.println("\nThe routing table is:");
        for (Route value : RoutingTable) {
            System.out.println(value);
        }

        ArrayList<Route> matches = new ArrayList<>(); //list of routes that work for a destination ip address

        System.out.println("\nThe router interfaces used for the corresponding destination IP addresses are:");

        //This nested loop checks a destination ip address to a list of provided routes
        for (String ip : IP_Addresses) {
            for (Route x : RoutingTable) {
                if (bitwise(ip, x.subnet_Mask).equals(x.networkID)) {
                    matches.add(x); //if a match is found add it to the list of matches
                }
            }
            // if there is more than one match, compare the subnets to find the longest matching prefix
            if (matches.size() > 1) {
                Route max = longestPrefixMatch(matches);
                System.out.println(max.nHop_Interface);
                matches.removeAll(matches); //empty list to reset the found matches
            } else { //otherwise, print the interface of the only match found!
                Route t = matches.get(0);
                System.out.println(t.nHop_Interface);
                matches.removeAll(matches); //empty list to reset the found matches
            }
        }
    }

    /** This is a bitwise method, which performs a "bitwise AND" calculation of an IP + subnet
     * Afterwards, it reconstructs the result by adding dots to match the correct format of an
     * IP address. Finally, it returns the result as a string.
     */
    public static String bitwise(String IP, String subnet) {
        //remove dots from both addresses
        String splitIP = IP.replace(".", "");
        String splitSub = subnet.replace(".", "");

        long a = Long.parseLong(splitIP, 2);
        long b = Long.parseLong(splitSub, 2);
        long finalValue = a & b; // bitwise AND operation

        String s = Long.toBinaryString(finalValue);
        // add a dot after every 8 digits to the result
        s = s.substring(0, 8) + "." + s.substring(8, 16) + "." + s.substring(16, 24) + "." + s.substring(24, 32);
        return s;
    }

    /**
     * This is a basic arraylist method, which compares the subnet mask addresses of routes
     * within an arraylist of routes.
     * It then returns the route with the largest subnet.
     *
     * Citation: A question posted by "Wrench" helped me with the logic of this method, the code they posted
     * can be found here https://stackoverflow.com/questions/26389860/generics-comparing-objects-in-an-arraylist
     * (Accessed April 14, 2021).
     */
    public static Route longestPrefixMatch(ArrayList<Route> temp) {
        //first, retrieve the first route in the list
        Route match = temp.get(0);
        for (Route x : temp) {
            //then, compare its subnet to all the routes in the list
            if (x.subnet_Mask.compareTo(match.subnet_Mask) > 0) {
                //finally, assign the route with the largest subnet to route match
                match = x;
            }
        }
        return match;
    }
}
