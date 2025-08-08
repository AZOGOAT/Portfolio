/* -----------------------------------------
  Have focus outline only for keyboard users 
 ---------------------------------------- */

const handleFirstTab = (e) => {
  if(e.key === 'Tab') {
    document.body.classList.add('user-is-tabbing')

    window.removeEventListener('keydown', handleFirstTab)
    window.addEventListener('mousedown', handleMouseDownOnce)
  }

}

const handleMouseDownOnce = () => {
  document.body.classList.remove('user-is-tabbing')

  window.removeEventListener('mousedown', handleMouseDownOnce)
  window.addEventListener('keydown', handleFirstTab)
}

window.addEventListener('keydown', handleFirstTab)

const backToTopButton = document.querySelector(".back-to-top");
let isBackToTopRendered = false;

let alterStyles = (isBackToTopRendered) => {
  backToTopButton.style.visibility = isBackToTopRendered ? "visible" : "hidden";
  backToTopButton.style.opacity = isBackToTopRendered ? 1 : 0;
  backToTopButton.style.transform = isBackToTopRendered
    ? "scale(1)"
    : "scale(0)";
};

window.addEventListener("scroll", () => {
  if (window.scrollY > 700) {
    isBackToTopRendered = true;
    alterStyles(isBackToTopRendered);
  } else {
    isBackToTopRendered = false;
    alterStyles(isBackToTopRendered);
  }
});

// WhatsApp QR Modal Logic
const whatsappLogo = document.querySelector(".footer__social-image[alt='Whatsapp']");
const qrModal = document.getElementById("whatsappModal");
const qrClose = document.querySelector(".qr-modal__close");

if (whatsappLogo && qrModal && qrClose) {
  whatsappLogo.addEventListener("click", (e) => {
    e.preventDefault();
    qrModal.style.display = "flex";
  });

  qrClose.addEventListener("click", () => {
    qrModal.style.display = "none";
  });

  window.addEventListener("click", (e) => {
    if (e.target === qrModal) {
      qrModal.style.display = "none";
    }
  });
}
document.querySelectorAll('.slider-container').forEach(container => {
  const slider = container.querySelector('.slider');
  let slides = slider.querySelectorAll('img, video');

  // Cloner la première et la dernière image pour un effet "infini"
  const firstClone = slides[0].cloneNode(true);
  const lastClone  = slides[slides.length - 1].cloneNode(true);
  slider.appendChild(firstClone);
  slider.insertBefore(lastClone, slides[0]);

  // Mettre à jour la liste des slides après clonage
  slides = slider.querySelectorAll('img, video'); // inclut les clones aussi

  const totalSlides = slides.length;
  let currentIndex  = 1; // on commence sur la 1ʳᵉ vraie image:contentReference[oaicite:1]{index=1}

  // Positionner le slider sur la 1ʳᵉ vraie image
  slider.style.transform = `translateX(-${currentIndex * 100}%)`;

  // Création des points (en ignorant les clones)
  const dotsContainer = document.createElement('div');
  dotsContainer.classList.add('dots');
  for (let i = 0; i < totalSlides - 2; i++) {
    const dot = document.createElement('span');
    dot.classList.add('dot');
    if (i === currentIndex - 1) dot.classList.add('active');
    dot.addEventListener('click', () => {
      currentIndex = i + 1; // +1 à cause du clone de début
      updateCarousel();
    });
    dotsContainer.appendChild(dot);
  }
  container.appendChild(dotsContainer);

  function updateDots() {
    const dots = dotsContainer.querySelectorAll('.dot');
    dots.forEach((dot, i) => {
      // L’index réel correspond à currentIndex-1 ; gérer les clones aux extrémités
      let activeIndex;
      if (currentIndex === 0) {
        activeIndex = totalSlides - 3;            // clone de fin → dernière vraie image:contentReference[oaicite:2]{index=2}
      } else if (currentIndex === totalSlides - 1) {
        activeIndex = 0;                         // clone de début → première vraie image:contentReference[oaicite:3]{index=3}
      } else {
        activeIndex = currentIndex - 1;
      }
      dot.classList.toggle('active', i === activeIndex);
    });
  }

  function updateCarousel() {
    slider.style.transition = 'transform 0.5s ease';
    slider.style.transform  = `translateX(-${currentIndex * 100}%)`;
    updateDots();
  }

  // Réinitialiser la position après l’animation lorsque l’on atteint un clone:contentReference[oaicite:4]{index=4}
  slider.addEventListener('transitionend', () => {
    // Si on est allé au clone de fin (dernière diapositive), repositionner sur la 1ʳᵉ vraie image
    if (currentIndex === totalSlides - 1) {
      slider.style.transition = 'none';
      currentIndex = 1;
      slider.style.transform = `translateX(-${currentIndex * 100}%)`;
    }
    // Si on est allé au clone de début (première diapositive), repositionner sur la dernière vraie image
    if (currentIndex === 0) {
      slider.style.transition = 'none';
      currentIndex = totalSlides - 2;
      slider.style.transform = `translateX(-${currentIndex * 100}%)`;
    }
    updateDots();
  });

  // Boutons
  container.querySelector('.next').addEventListener('click', () => {
    currentIndex++;
    updateCarousel();
  });
  container.querySelector('.prev').addEventListener('click', () => {
    currentIndex--;
    updateCarousel();
  });
});

// Initialisation du modal
const modal      = document.getElementById('galleryModal');
const modalSlider= modal.querySelector('.slider');
const modalDots  = modal.querySelector('.dots');
const modalPrev  = modal.querySelector('.prev');
const modalNext  = modal.querySelector('.next');
const modalClose = modal.querySelector('.close-button');

function openGallery(slides, clickedIndex) {
  // "slides" est une NodeList des images du carrousel cliqué (avec clones)
  // "clickedIndex" est l’index dans cette NodeList de l’image sur laquelle on a cliqué

  // Préparer les données réelles en ignorant les clones
  const realCount = slides.length - 2; // 2 clones
  const imagesData = [];
  for (let i = 1; i <= realCount; i++) {
  const el = slides[i];
  imagesData.push({
    src: el.src,
    alt: el.alt || "",
    type: el.tagName.toLowerCase()  // 'img' ou 'video'
  });
}


  // Déterminer l’index réel à partir de l’index cliqué
  let startIndex;
  if (clickedIndex === 0) {
    startIndex = realCount - 1;
  } else if (clickedIndex === slides.length - 1) {
    startIndex = 0;
  } else {
    startIndex = clickedIndex - 1;
  }

  // Nettoyer le contenu précédent
  modalSlider.innerHTML = '';
  modalDots.innerHTML   = '';

  // Construire les slides avec clones (dernier, réels, premier) pour le modal:contentReference[oaicite:4]{index=4}
  const slidesData = [
    imagesData[imagesData.length - 1],  // clone du dernier
    ...imagesData,
    imagesData[0]                       // clone du premier
  ];
  slidesData.forEach(data => {
  let element;
  if (data.type === 'video') {
    element = document.createElement('video');
    element.src = data.src;
    element.controls = true;
    element.muted = true;
    element.playsInline = true;
    element.setAttribute('class', 'work__image');
  } else {
    element = document.createElement('img');
    element.src = data.src;
    element.alt = data.alt;
    element.setAttribute('class', 'work__image');
  }
  modalSlider.appendChild(element);
});


  // Créer les points indicateurs
  imagesData.forEach((_, i) => {
    const dot = document.createElement('span');
    dot.classList.add('dot');
    if (i === startIndex) dot.classList.add('active');
    dot.addEventListener('click', () => {
      currentIndex = i + 1;
      updateModal();
    });
    modalDots.appendChild(dot);
  });

  // Index courant (tient compte des clones)
  let currentIndex = startIndex + 1;

  function updateDots() {
    const dots = modalDots.querySelectorAll('.dot');
    dots.forEach((dot, i) => {
      // index actif corrigé (0..realCount-1):contentReference[oaicite:5]{index=5}
      let activeIndex;
      if (currentIndex === 0) {
        activeIndex = imagesData.length - 1;
      } else if (currentIndex === slidesData.length - 1) {
        activeIndex = 0;
      } else {
        activeIndex = currentIndex - 1;
      }
      dot.classList.toggle('active', i === activeIndex);
    });
  }

  function updateModal(animate = true) {
    modalSlider.style.transition = animate ? 'transform 0.5s ease' : 'none';
    modalSlider.style.transform  = `translateX(-${currentIndex * 100}%)`;
    updateDots();
  }

  // Boucle infinie : repositionner sur les vraies images après la transition:contentReference[oaicite:6]{index=6}
  modalSlider.addEventListener('transitionend', () => {
    if (currentIndex === 0) {
      modalSlider.style.transition = 'none';
      currentIndex = imagesData.length;
      modalSlider.style.transform = `translateX(-${currentIndex * 100}%)`;
    } else if (currentIndex === slidesData.length - 1) {
      modalSlider.style.transition = 'none';
      currentIndex = 1;
      modalSlider.style.transform = `translateX(-${currentIndex * 100}%)`;
    }
    updateDots();
  });

  // Navigation dans le modal
  modalNext.onclick = () => {
    currentIndex++;
    updateModal();
  };
  modalPrev.onclick = () => {
    currentIndex--;
    updateModal();
  };
  modalClose.onclick = () => {
    modal.classList.remove('active');
  };

  // Afficher le modal et positionner sur l’image cliquée
  modal.classList.add('active');
  updateModal(false);
}

// Ajouter l’événement d’ouverture sur chaque image du carrousel de la page
document.querySelectorAll('.slider-container').forEach(container => {
  const slides = container.querySelectorAll('.slider img, .slider video');
  slides.forEach((img, index) => {
    img.addEventListener('click', () => {
      openGallery(slides, index);
    });
  });
});

/* -----------------------------------------
  Skills Tab Navigation
 ---------------------------------------- */
document.querySelectorAll('.section-header button').forEach(button => {
  button.addEventListener('click', function () {
    // Désactiver tous les onglets et sections
    document.querySelectorAll('.section-header button').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.section-content').forEach(content => content.classList.remove('active'));
    // Activer l’onglet cliqué
    this.classList.add('active');
    // Afficher la section correspondante
    const targetId = this.getAttribute('data-target');
    document.getElementById(targetId).classList.add('active');
  });
});


/* -----------------------------------------
  Resume Modal Logic
 ---------------------------------------- */
const resumeToggle = document.querySelector('.resume-toggle');
const resumeMenu   = document.querySelector('.resume-menu');
const resumeOptions = document.querySelectorAll('.resume-option');

const cvModal  = document.getElementById('cvModal');
const cvFrame  = document.getElementById('cvFrame');
const cvClose  = document.querySelector('.cv-modal__close');

resumeOptions.forEach(option => {
  option.addEventListener('click', (e) => {
    e.preventDefault();
    const pdfFile = option.dataset.file;
    if (pdfFile) {
      cvFrame.src = pdfFile;
    }
    cvModal.style.display = 'flex';
  });
});

cvClose.addEventListener('click', () => {
  cvModal.style.display = 'none';
  cvFrame.src = '';
});

window.addEventListener('click', (e) => {
  if (e.target === cvModal) {
    cvModal.style.display = 'none';
    cvFrame.src = '';
  }
});


/* -----------------------------------------
  Contact Form Submission
 ---------------------------------------- */
document.getElementById("contactForm").addEventListener("submit", function (e) {
  e.preventDefault();
  const form = e.target;

  fetch(form.action, {
    method: "POST",
    body: new FormData(form),
    headers: {
      Accept: "application/json"
    }
  }).then(response => {
    if (response.ok) {
      alert("🎉 Thank you! Your message has been sent.");
      form.reset();
    } else {
      alert("⚠️ Oops! There was a problem submitting your form.");
    }
  }).catch(error => {
    alert("❌ Something went wrong. Please try again later.");
  });
});

/* -----------------------------------------
  Google Analytics
 ---------------------------------------- */

// TRACKING DES CLICS SUR IMAGES/VIDÉOS 
document.querySelectorAll('.work__box .slider img, .work__box .slider video').forEach(media => {
  media.addEventListener('click', () => {
    // On remonte jusqu'au bloc .work__box pour récupérer le titre <h3>
    const box         = media.closest('.work__box');
    const projectName = box ? box.querySelector('h3').textContent.trim() : 'Inconnu';
    // On récupère l'indice de l'image/vidéo dans le carrousel (facultatif)
    const index = Array.from(media.parentElement.children).indexOf(media) + 1;
    gtag('event', 'image_click', {
      event_category: 'Galerie',
      project_name: projectName,
      media_index: index,
      media_type: media.tagName.toLowerCase(), // img ou video
      media_source: media.src
    });
  });
});

// TRACKING DES CLICS SUR LES LIENS "Visit Site"
document.querySelectorAll('.work__box .work__links > a.link__text').forEach(lien => {
  lien.addEventListener('click', () => {
    const box         = lien.closest('.work__box');
    const projectName = box ? box.querySelector('h3').textContent.trim() : 'Inconnu';
    gtag('event', 'visit_project_link', {
      event_category: 'Navigation',
      project_name: projectName,
      link_url: lien.href
    });
  });
});


// MESURER LE TEMPS PASSÉ PAR SECTION
const sections = {
  work:   { element: document.getElementById('work'),   start: null, total: 0 },
  skills: { element: document.getElementById('skills'), start: null, total: 0 },
  about:  { element: document.getElementById('about'),  start: null, total: 0 },
  contact:{ element: document.getElementById('contact'),start: null, total: 0 },
};

// Détecter l’entrée et la sortie d’une section dans la fenêtre
const observer = new IntersectionObserver((entries) => {
  entries.forEach(entry => {
    const sectionKey = entry.target.dataset.section; 
    const obj = sections[sectionKey];
    if (!obj) return;
    if (entry.isIntersecting) {
      // L’utilisateur voit la section → démarrer le chrono
      obj.start = performance.now();
    } else {
      // L’utilisateur quitte la section → ajouter la durée
      if (obj.start !== null) {
        obj.total += performance.now() - obj.start;
        obj.start = null;
      }
    }
  });
}, { threshold: 0.5 }); // section considérée comme “vue” à 50 % de la hauteur


Object.keys(sections).forEach(key => {
  const el = sections[key].element;
  if (el) {
    el.dataset.section = key;
    observer.observe(el);
  }
});

// Quand la page est sur le point d’être quittée, envoyer les durées
window.addEventListener('beforeunload', () => {
  Object.keys(sections).forEach(key => {
    const obj = sections[key];
    if (obj.start !== null) {
      // ajouts en cours si la section est encore visible
      obj.total += performance.now() - obj.start;
    }
    const dureeSec = Math.round(obj.total / 1000);
    if (dureeSec > 0) {
      gtag('event', 'section_engagement', {
        event_category: 'Temps par section',
        section_name: key,
        duration_seconds: dureeSec
      });
    }
  });
});

//Suivre l’ouverture du menu “Resume” et le téléchargement des CV
document.querySelector('.resume-toggle').addEventListener('click', () => {
  gtag('event', 'resume_open', { event_category: 'CV' });
});
document.querySelectorAll('.resume-option').forEach(option => {
  option.addEventListener('click', () => {
    const version = option.dataset.file.includes('Short') ? 'court' : 'long';
    gtag('event', 'cv_download', {
      event_category: 'CV',
      version: version
    });
  });
});

//Mesurer la profondeur de défilement (scroll depth)
let lastScrollEvent = 0;
window.addEventListener('scroll', () => {
  const scrollPercent = (window.scrollY + window.innerHeight) / document.body.scrollHeight;
  const percents = [0.25, 0.5, 0.75, 1];
  percents.forEach(p => {
    if (scrollPercent >= p && lastScrollEvent < p) {
      gtag('event', 'scroll_depth', {
        event_category: 'Engagement',
        percent: p * 100
      });
      lastScrollEvent = p;
    }
  });
});

// Clic sur l’icône GitHub
const githubIcon = document.querySelector('.footer__social-image[alt="Github"]');
if (githubIcon) {
  githubIcon.addEventListener('click', () => {
    gtag('event', 'social_github_click', {
      event_category: 'Réseaux sociaux',
      event_label: 'GitHub',
      platform_url: 'https://github.com/AZOGOAT'
    });
  });
}

// Clic sur l’icône WhatsApp
const whatsappIcon = document.querySelector('.footer__social-image[alt="Whatsapp"]');
if (whatsappIcon) {
  whatsappIcon.addEventListener('click', () => {
    gtag('event', 'social_whatsapp_click', {
      event_category: 'Réseaux sociaux',
      event_label: 'WhatsApp'
      // pas de platform_url car le lien ouvre simplement le QR code
    });
  });
}

// Clic sur l’icône LinkedIn
const linkedinIcon = document.querySelector('.footer__social-image[alt="Linkedin"]');
if (linkedinIcon) {
  linkedinIcon.addEventListener('click', () => {
    gtag('event', 'social_linkedin_click', {
      event_category: 'Réseaux sociaux',
      event_label: 'LinkedIn',
      platform_url: 'https://www.linkedin.com/in/omar-ziyad-azgaoui'
    });
  });
}