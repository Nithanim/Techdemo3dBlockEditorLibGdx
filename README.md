This repository holds "tech-demo" of an editor in a 3d block-based world ussing LibGDX.
The user controls a free-cam and can select a 3d area of qubes using two points and also modify the selection afterwards. For the main selected block only, rotation and copy-paste are implemented.

The selection of blocks is done using ray-casting.

The code is extremely messy and also contains some modified code copied from LibGDX itself. Heed their license.
An example for that is the EditorCamera which was overhauled quite a bit.
Also the Vector3i is a specialized copy of Vector3.

You should only use bits and peaces you find useful because the thing is just cobbled together.

