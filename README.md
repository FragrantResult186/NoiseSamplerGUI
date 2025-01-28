# NoiseSamplerGUI  
This is a Java GUI application designed to help users efficiently explore Minecraft seeds. It utilizes two external repositories: [noise-sampler](https://github.com/KalleStruik/noise-sampler) and [seed-checker](https://github.com/jellejurre/seed-checker/tree/1.18.1).  

We respect the licenses of the repositories used in this project. The original projects are openly available on GitHub, and we ensure compliance with their licensing terms when integrating them into this application.  

---

## Features  

### **Seed Exploration**  
Search for seeds using noise and height filters.  
(Note: Height checks are computationally expensive. It is recommended to add noise conditions first before adding height-related filters.)  
The application supports three methods of exploration:  
1. **Exhaustive Search**  
2. **Bitmask Search (32-bit and 48-bit)**  
3. **Search using an Input File**  

### **Seed Viewer**  
This feature emulates blocks from a seed and displays a cross-sectional map.  
- The process can be computationally intensive, so performance might be slow.  
- Note: As seed-checker supports Minecraft version 1.18.1, structures like Ancient Cities and Trial Chambers are not generated.  

### **Save and Load**  
You can save noise and height conditions in JSON format for future use.  
- Saved files allow you to reload previous configurations easily.  
- Additionally, seed results and conditions are automatically saved when the application is shut down.  

---

## Credits  
- [jellejurre](https://github.com/jellejurre)  
- [KalleStruik](https://github.com/KalleStruik)  
