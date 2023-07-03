import {updateNav} from "./modules/nav.js";
import {createTeacherModal} from "./modules/modal-generator.js";
import {getMonthNumber, initializeCalendar} from "./modules/calendar.js";
import {getCurrentId, getTeachers} from "./api/queries.js";
import {loadInstruments} from "./modules/filter.js";
import {addEventToBookLessons} from "./api/constants.js";

document.addEventListener("DOMContentLoaded", async () => {
    import("./modules/modal.js");
    import("./modules/calendar.js");
    import("./modules/filter.js");

    await updateNav();
    await loadInstruments();
    await initializeCalendar();
    await teacherLoadingAndSearch();
    const currentId = parseInt(await getCurrentId());
    addEventToBookLessons(currentId);

    const mapIcons = {
        "accordion" : "../static/images/map-icons/accordion.svg",
        "bugle" : "../static/images/map-icons/bugle.svg",
        "clarinet" : "../static/images/map-icons/clarinet.svg",
        "classic-guitar" : "../static/images/map-icons/classic-guitar.svg",
        "cymbals" : "../static/images/map-icons/cymbals.svg",
        "drums" : "../static/images/map-icons/drums.svg",
        "electro-guitar" : "../static/images/map-icons/electro-guitar.svg",
        "flute" : "../static/images/map-icons/flute.svg",
        "harp" : "../static/images/map-icons/harp.svg",
        "lyre" : "../static/images/map-icons/lyre.svg",
        "piano" : "../static/images/map-icons/piano.svg",
        "picolo" : "../static/images/map-icons/picolo.svg",
        "saxophone" : "../static/images/map-icons/saxophone.svg",
        "synthesizer" : "../static/images/map-icons/synthesizer.svg",
        "trumpet" : "../static/images/map-icons/trumpet.svg",
        "tuba" : "../static/images/map-icons/tuba.svg",
        "violin" : "../static/images/map-icons/violin.svg",
        "voice" : "../static/images/map-icons/voice.svg",
        "xylophone" : "../static/images/map-icons/xylophone.svg",
        "default" : "../static/images/map-icons/default.svg",
        "special" : "../static/images/map-icons/special.svg",

        114 : "../static/images/map-icons/accordion.svg",
        115 : "../static/images/map-icons/bugle.svg",
        116 : "../static/images/map-icons/clarinet.svg",
        117 : "../static/images/map-icons/classic-guitar.svg",
        118 : "../static/images/map-icons/cymbals.svg",
        119 : "../static/images/map-icons/drums.svg",
        120 : "../static/images/map-icons/electro-guitar.svg",
        121 : "../static/images/map-icons/flute.svg",
        122 : "../static/images/map-icons/harp.svg",
        123 : "../static/images/map-icons/lyre.svg",
        124 : "../static/images/map-icons/piano.svg",
        125 : "../static/images/map-icons/picolo.svg",
        126 : "../static/images/map-icons/saxophone.svg",
        127 : "../static/images/map-icons/synthesizer.svg",
        128 : "../static/images/map-icons/trumpet.svg",
        129 : "../static/images/map-icons/tuba.svg",
        130 : "../static/images/map-icons/violin.svg",
        131 : "../static/images/map-icons/voice.svg",
        132 : "../static/images/map-icons/xylophone.svg",
    }

    // initialize before everything
    const ZIP_LOCATIONS = 'ZIP_LOCATIONS';
    let storage_coordinates = loadZipLocations();
    // console.log('retrieved zips:', storage_coordinates);

    const map = initializeMap();
    await setMapCenter(await getProjectedCoordinate('enschede'));


    // ------------------- FUNCTIONS -------------------
    //Initialize map
    function initializeMap() {
        return new ol.Map({
            layers: [new ol.layer.Tile({source: new ol.source.OSM()})],
            view: new ol.View({
                center: [0, 0],
                zoom: 0,
                maxZoom: 19,
            }),
            target: 'js-map',
        });
    }
    //Load zipcodes
    function loadZipLocations() {
        const zips = localStorage.getItem(ZIP_LOCATIONS);
        return zips ? JSON.parse(zips) : {};
    }
    //Get coordinates of a query
    async function getCoordinate(query) {
        query = query.toUpperCase();
    
        let coordinate = [];
        let coordinateRetrieved = false;
    
        if (storage_coordinates.hasOwnProperty(query)) {
            coordinate = storage_coordinates[query];
            console.log('Retrieved from localStorage coordinate:', query, coordinate);
            coordinateRetrieved = true;
        }
    
        try {
            if (!coordinateRetrieved) {
                const url = '/api/zipcode-coordinates/' + query;
                const response = await fetch(url);
                const data = await response.text();
    
                if (data) {
                    try {
                        const jsonData = JSON.parse(data);
                        coordinate = [jsonData.longitude, jsonData.latitude];
                        console.log('Retrieved from db:', query, coordinate);
                        coordinateRetrieved = true;
                        addToLocalStorage(query, coordinate);
                    } catch (e) {
                        console.log('Error parsing JSON db:', e);
                    }
                }
            }
        } catch (e) {
            console.log('Error in database fetch:', e);
        }
    
        try {
            if (!coordinateRetrieved) {
                const url = 'https://nominatim.openstreetmap.org/search?q=' + query + '&format=geojson&limit=1';
                const response = await fetch(url);
                const data = await response.json();

                if (data.features && data.features.length > 0) {
                    try {
                        coordinate = data.features[0].geometry.coordinates;
                        console.log('Retrieved from nominatim:', query, coordinate);
                        coordinateRetrieved = true;
                        addToLocalStorage(query, coordinate);
                    } catch (e) {
                        console.log('Error parsing JSON nominatim:', e);
                    }
                }
            }
        } catch (e) {
            console.log('Error in Nominatim fetch:', e);
        }

        if (!coordinateRetrieved) {
            coordinate = await getCoordinate('enschede');
        }
        return coordinate;
    }
    //Get projected coordinates for the map
    async function getProjectedCoordinate(query) {
        return ol.proj.fromLonLat(await getCoordinate(query));
    }
    //Adding data to the local storage
    function addToLocalStorage(key, value) {
        storage_coordinates[key] = value;
        localStorage.setItem(ZIP_LOCATIONS, JSON.stringify(storage_coordinates));
        console.log('stored in localStorage:', key, value);
    }
    //Adjusting the map zooming and positioning
    async function setMapCenter(coordinate) {
        if (coordinate) {
            const view = map.getView();
            view.animate({
                center: coordinate,
                zoom: 12,
                duration: 2500,
                easing: ol.easing.easeOut,
            });
        }
    }
    //Extracting teacher data from fetch
    async function extractTeachers(data) {
        const teachersWithInstruments = [];
        const uniqueTeachers = new Set();

        for (const teacher of data) {
            if (!uniqueTeachers.has(teacher.id) && teacher.zipcode !== null && teacher.zipcode !== 'null' && teacher.zipcode !== 'UNKNOWN') {
                const projectedCoordinates = await getProjectedCoordinate(teacher.zipcode);

                const response = await fetch('/api/teacher-instruments/' + teacher.id);
                const instrumentData = await response.json();
                // console.log('instrument data', teacher.id, instrumentData);

                if (instrumentData.length > 0) {
                    teachersWithInstruments.push({
                        id: teacher.id,
                        zip: teacher.zipcode,
                        instrumentData: instrumentData,
                        projectedCoordinates,
                    });
                    uniqueTeachers.add(teacher.id);
                }
            }
        }

        return teachersWithInstruments;
    }


    //Teacher loading and search according to specified filters
    function teacherLoadingAndSearch() {

        const teacherRequest = {
            instrumentId : "%%",
            skillId : "%%",
            rating : "-1",
            date : "%%",
            online : "%%",
        };
        const searchInput = document.getElementById("search-location");

        let timeoutId; // Variable to store the timeout ID
        // Adjusting lessonsLocation when in search bar
        searchInput.addEventListener("input", (event) => {
            event.preventDefault();
            // Clear the previous timeout if exists
            clearTimeout(timeoutId);
            // Set a new timeout of 2 seconds
            timeoutId = setTimeout(async () => {
                if (searchInput.value) {
                    await setMapCenter(await getProjectedCoordinate(searchInput.value));
                }
            }, 2000); // 2000 milliseconds = 2 seconds
        });

        // Initial proccessing of the map
        processMap(teacherRequest);

        document.querySelector(".filters__reset-filters").addEventListener("click", () => {
            teacherRequest.instrumentId = "%%";
            teacherRequest.skillId = "%%";
            teacherRequest.rating = "-1";
            teacherRequest.date = "%%";
            teacherRequest.online = "%%";
            processMap(teacherRequest);
        })


        // Adjusting lessonsInstruments when selected in filters
        document.querySelector("#instruments-list").addEventListener("click", (event) => {
            const selectedInstrument = event.target.closest(".filters__option")
            if (selectedInstrument) {
                teacherRequest.instrumentId = selectedInstrument.getAttribute("for");
                processMap(teacherRequest, selectedInstrument.getAttribute("for"));
            }
        });

        // Adjusting skill level when selected in filters
        document.querySelector("#level-list").addEventListener("click", (event) => {
            const selectedSkill = event.target.closest(".filters__option")
            if (selectedSkill) {
                teacherRequest.skillId = parseInt(selectedSkill.getAttribute("for").charAt(0));
                processMap(teacherRequest);
            }
        });

        // Adjusting lessons type when selected in filters
        document.querySelector("#type-list").addEventListener("click", (event) => {
            const selectedType = event.target.closest(".filters__option")
            if (selectedType) {
                teacherRequest.online = selectedType.getAttribute("for");
                processMap(teacherRequest);
            }
        });
        // Adjusting teachers rating when selected in filters
        document.querySelector("#rating-list").addEventListener("click", (event) => {
            const selectedRating = event.target.closest(".filters__option")
            if (selectedRating) {
                teacherRequest.rating = parseInt(selectedRating.getAttribute("for").charAt(0));
                processMap(teacherRequest);
            }
        });

        // Adjusting teachers availability when selected in filters
        document.querySelector("#calendar-schedule").addEventListener("click", (event) => {
            const selectedDay = event.target.closest(".selected-day")
            if (selectedDay) {
                const year = document.querySelector("#month-year").innerHTML.split(" ")[1];
                const month = getMonthNumber(document.querySelector("#month-year").innerHTML.split(" ")[0]);
                const day = selectedDay.innerHTML.padStart(2, '0');
                teacherRequest.date =`${year}-${month}-${day}`;
                processMap(teacherRequest);
            }
        });
    }
    //Fetching teachers
    async function fetchTeachers(teacherRequest) {
        const data = await getTeachers(teacherRequest);
        const teachers = await extractTeachers(data);
        return teachers;
    }
    //Creating markers
    function createMarker(teacher, instrumentId) {
        let markerIcon = mapIcons.default;

        if (teacher.instrumentData) {
            if (instrumentId) {
                markerIcon = mapIcons[instrumentId];
            } else {
                markerIcon = mapIcons[teacher.instrumentData[0].id];
            }
        }


        let maxOffset = 150;

        let offsetX = (Math.random() - 0.6) * 2 * maxOffset;
        let offsetY = (Math.random() - 0.6) * 2 * maxOffset;

        let newX = teacher.projectedCoordinates[1] + offsetX;
        let newY = teacher.projectedCoordinates[0] + offsetY;


        return new ol.Feature({
            geometry: new ol.geom.Point([newY, newX]),
            id: teacher.id,
            markerIcon: markerIcon,
        });
    }
    //Map process according to the parameters indicated
    async function processMap(teacherRequest, instrumentId) {
        console.log('teacherRequest:', teacherRequest);
        
        const data = await fetchTeachers(teacherRequest);
        console.log('fetch completed', data);

        if (!data) {
            console.log('empty list of teachers')
            return;
        }

        const clusterDistance = 40;
        const clusterMinDistance = 20;
        let clusterFeatures = data.map((teacher) => {
            return createMarker(teacher, instrumentId);
        });

        const source = new ol.source.Vector({
            features: clusterFeatures,
        });

        map.getLayers().forEach((layer) => {
            if (layer instanceof ol.layer.Vector) {
                layer.setSource(new ol.source.Vector());
            }
        });

        const clusterSource = new ol.source.Cluster({
            distance: clusterDistance,
            minDistance: clusterMinDistance,
            source: source,
        });

        const circleStyle = new ol.style.Circle({
            scale: [1, 0.45],
            radius: 50,
            fill: new ol.style.Fill({color: 'rgba(138, 62, 183, 0.29)'}),
            stroke: new ol.style.Stroke({
                color: 'rgba(138, 62, 183, 1)',
                width: 2,
            }),
        });

        const styleCache = {};
        const clusters = new ol.layer.Vector({
            source: clusterSource,
            style: function(feature) {
                const size = feature.get('features').length;

                if (size === 1) {
                    const markerIcon = feature.get('features')[0].get('markerIcon');
                    const style = [
                        new ol.style.Style({image: circleStyle}),
                        new ol.style.Style({
                            image: new ol.style.Icon({
                                anchor: [0.5, 1],
                                src: markerIcon,
                            }),
                        }),
                        
                    ]
                    return style;
                }

                if (size > 1) {
                    let style = styleCache[size];
                    if (!style) {
                        style = new ol.style.Style({
                            zIndex: size,
                            image: new ol.style.Circle({
                                radius: 20,
                                stroke: new ol.style.Stroke({
                                    color: '#fff',
                                }),
                                fill: new ol.style.Fill({
                                    color: '#8A3EB7',
                                }),
                            }),
                            text: new ol.style.Text({
                                text: size.toString(),
                                fill: new ol.style.Fill({
                                    color: '#fff',
                                }),
                            }),
                        });
                        styleCache[size] = style;
                    }
                    return style;
                }
            }
        });
        map.addLayer(clusters);

        map.on('click', async (e) => {
            await handleMapClick(e, clusters);
        });
    }

    async function handleMapClick(e, clusters) {
        clusters.getFeatures(e.pixel).then( async (clickedFeatures) => {
            if (clickedFeatures.length) {
                // Get clustered coordinates
                const features = clickedFeatures[0].get('features');
                if (features.length === 1) {
                    const feature = features[0];
                    const id = feature.get('id');
                    await displayProfileModal(id);
                }
                if (features.length > 1) {
                    const extent = new ol.extent.boundingExtent(
                        features.map((r) => r.getGeometry().getCoordinates())
                    );
                    map.getView().fit(extent, {
                        duration: 1000,
                        padding: [100, 100, 100, 100],
                    });
                }
            }
        });
    }
    //Displaying teacher modal on click
    async function displayProfileModal(id) {
        const profileModalIfExists = document.querySelector(".modal-" + id);
        if (profileModalIfExists) {
            profileModalIfExists.classList.remove("none");
        } else {
            const profileModal = await createTeacherModal(id);
            document.querySelector("body").insertAdjacentHTML("afterbegin", profileModal);
            document.querySelectorAll(".modal").forEach((modal) => {
                modal.addEventListener("click", (e) => {
                    if (e.target.closest(".modal__close")) {
                        e.target.parentElement.parentElement.parentElement.classList.add("none");
                    }
                })
            })
        }
    }
});