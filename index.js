/* -----------------------------------------
  Have focus outline only for keyboard users 
 ---------------------------------------- */

const handleFirstTab = (e) => {
  if (e.key === 'Tab') {
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

// ----------- Initialisation des sliders avec dots + infini -----------
document.querySelectorAll('.slider-container').forEach(container => {
  const slider = container.querySelector('.slider');
  let slides = slider.querySelectorAll('img, video');

  // Cloner pour effet infini
  const firstClone = slides[0].cloneNode(true);
  const lastClone = slides[slides.length - 1].cloneNode(true);
  slider.appendChild(firstClone);
  slider.insertBefore(lastClone, slides[0]);
  slides = slider.querySelectorAll('img, video');

  const totalSlides = slides.length;
  let currentIndex = 1;
  slider.style.transform = `translateX(-${currentIndex * 100}%)`;

  // Dots
  const dotsContainer = document.createElement('div');
  dotsContainer.classList.add('dots');
  for (let i = 0; i < totalSlides - 2; i++) {
    const dot = document.createElement('span');
    dot.classList.add('dot');
    if (i === 0) dot.classList.add('active');
    dot.addEventListener('click', () => {
      currentIndex = i + 1;
      updateCarousel();
    });
    dotsContainer.appendChild(dot);
  }
  container.appendChild(dotsContainer);

  function updateDots() {
    const dots = dotsContainer.querySelectorAll('.dot');
    let activeIndex = (currentIndex === 0)
      ? totalSlides - 3
      : (currentIndex === totalSlides - 1)
        ? 0
        : currentIndex - 1;
    dots.forEach((dot, i) => dot.classList.toggle('active', i === activeIndex));
  }

  function updateCarousel() {
    slider.style.transition = 'transform 0.5s ease';
    slider.style.transform = `translateX(-${currentIndex * 100}%)`;
    updateDots();
  }

  slider.addEventListener('transitionend', () => {
    if (currentIndex === totalSlides - 1) {
      slider.style.transition = 'none';
      currentIndex = 1;
      slider.style.transform = `translateX(-${currentIndex * 100}%)`;
    }
    if (currentIndex === 0) {
      slider.style.transition = 'none';
      currentIndex = totalSlides - 2;
      slider.style.transform = `translateX(-${currentIndex * 100}%)`;
    }
    updateDots();
  });

  container.querySelector('.next').addEventListener('click', () => {
    currentIndex++;
    updateCarousel();
  });
  container.querySelector('.prev').addEventListener('click', () => {
    currentIndex--;
    updateCarousel();
  });

  // Ouverture du modal au clic sur une image/vidéo
  slides.forEach((media, index) => {
    media.addEventListener('click', () => openGallery(slides, index));
  });

  // ---- Swipe/touch navigation (mobile) ----
  let startX = 0, currentX = 0, dragging = false, swiped = false;

  const touchStart = (e) => {
    const t = e.touches ? e.touches[0] : e;
    startX = currentX = t.clientX;
    dragging = true;
    swiped = false;
  };

  const touchMove = (e) => {
    if (!dragging) return;
    const t = e.touches ? e.touches[0] : e;
    currentX = t.clientX;
    // Si vrai swipe horizontal, on bloque le scroll vertical
    if (Math.abs(currentX - startX) > 10 && e.cancelable) e.preventDefault();
  };

  const touchEnd = () => {
    if (!dragging) return;
    const dx = currentX - startX;
    const THRESHOLD = 40; // px
    if (dx <= -THRESHOLD) { currentIndex++; updateCarousel(); swiped = true; }
    else if (dx >= THRESHOLD) { currentIndex--; updateCarousel(); swiped = true; }
    dragging = false;
  };

  container.addEventListener('touchstart', touchStart, { passive: true });
  container.addEventListener('touchmove', touchMove, { passive: false });
  container.addEventListener('touchend', touchEnd, { passive: true });

  // Si un swipe vient d'avoir lieu, on empêche l'ouverture de la lightbox au "click"
  container.addEventListener('click', (e) => {
    if (swiped) { e.stopPropagation(); e.preventDefault(); swiped = false; }
  }, true);

});

// Modal initialization
const modal = document.getElementById('galleryModal');
const modalSlider = modal.querySelector('.slider');
const modalDots = modal.querySelector('.dots');
const modalPrev = modal.querySelector('.prev');
const modalNext = modal.querySelector('.next');
const modalClose = modal.querySelector('.close-button');

function openGallery(slides, clickedIndex) {
  // "slides" is a NodeList of the clicked carousel’s images (with clones)
  // "clickedIndex" is the index in that NodeList of the clicked image

  // Prepare real data ignoring clones
  const realCount = slides.length - 2; // 2 clones
  const imagesData = [];
  for (let i = 1; i <= realCount; i++) {
    const el = slides[i];
    imagesData.push({
      src: el.src,
      alt: el.alt || "",
      type: el.tagName.toLowerCase()  // 'img' or 'video'
    });
  }

  // Determine the real index from the clicked index
  let startIndex;
  if (clickedIndex === 0) {
    startIndex = realCount - 1;
  } else if (clickedIndex === slides.length - 1) {
    startIndex = 0;
  } else {
    startIndex = clickedIndex - 1;
  }

  // Clear previous content
  modalSlider.innerHTML = '';
  modalDots.innerHTML = '';

  // Build slides with clones (last, reals, first) for the modal
  const slidesData = [
    imagesData[imagesData.length - 1],  // last clone
    ...imagesData,
    imagesData[0]                       // first clone
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

  // Create the indicator dots
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

  // Current index (includes clones)
  let currentIndex = startIndex + 1;

  function updateDots() {
    const dots = modalDots.querySelectorAll('.dot');
    dots.forEach((dot, i) => {
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
    modalSlider.style.transform = `translateX(-${currentIndex * 100}%)`;
    updateDots();
  }

  // Infinite loop: reposition on real images after the transition
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

  // ---- Swipe/touch pour la galerie (modal) — attacher au conteneur global ----
  const modalContent = modal.querySelector('.gallery-content'); // capte le geste même sur <video>
  let mStartX = 0, mCurrentX = 0, mDragging = false;

  const mStart = (e) => {
    const t = e.touches ? e.touches[0] : e;
    mStartX = t.clientX;
    mCurrentX = mStartX;
    mDragging = true;
  };
  const mMove = (e) => {
    if (!mDragging) return;
    const t = e.touches ? e.touches[0] : e;
    mCurrentX = t.clientX;
    if (Math.abs(mCurrentX - mStartX) > 10 && e.cancelable) e.preventDefault(); // on gère le pan-x
  };
  const mEnd = () => {
    if (!mDragging) return;
    const dx = mCurrentX - mStartX;
    const THRESHOLD = 40;
    if (dx <= -THRESHOLD) { currentIndex++; updateModal(); }
    else if (dx >= THRESHOLD) { currentIndex--; updateModal(); }
    mDragging = false;
  };

  modalContent.addEventListener('touchstart', mStart, { passive: true });
  modalContent.addEventListener('touchmove', mMove, { passive: false });
  modalContent.addEventListener('touchend', mEnd, { passive: true });

  // Modal navigation
  modalNext.onclick = () => { currentIndex++; updateModal(); };
  modalPrev.onclick = () => { currentIndex--; updateModal(); };
  modalClose.onclick = () => { modal.classList.remove('active'); };

  // Show the modal and position on the clicked image
  modal.classList.add('active');
  updateModal(false);
}

/* -----------------------------------------
  Skills Tab Navigation
 ---------------------------------------- */
document.querySelectorAll('.section-header button').forEach(button => {
  button.addEventListener('click', function () {
    // Deactivate all tabs and sections
    document.querySelectorAll('.section-header button').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.section-content').forEach(content => content.classList.remove('active'));
    // Activate the clicked tab
    this.classList.add('active');
    // Show the corresponding section
    const targetId = this.getAttribute('data-target');
    document.getElementById(targetId).classList.add('active');
  });
});

/* -----------------------------------------
  Skills Info Tooltips (click on mobile, hover on desktop)
------------------------------------------ */
(() => {
  const icons = document.querySelectorAll('.skill-item .info-icon');

  const getTooltip = (icon) => {
    // Create the tooltip on the fly if needed
    let tip = icon.querySelector('.info-tooltip');
    if (!tip) {
      tip = document.createElement('div');
      tip.className = 'info-tooltip';
      // Get the text from the existing title attribute
      const txt = icon.getAttribute('title') || icon.getAttribute('data-info') || '';
      tip.textContent = txt;
      icon.appendChild(tip);
      // Remove the native title to avoid double-tooltips
      if (icon.hasAttribute('title')) icon.removeAttribute('title');
      icon.setAttribute('data-info', txt);
    }
    return tip;
  };

  const showTip = (icon) => {
    const tip = getTooltip(icon);
    tip.classList.add('show');
    icon.setAttribute('aria-expanded', 'true');
  };

  const hideTip = (icon) => {
    const tip = icon.querySelector('.info-tooltip');
    if (tip) tip.classList.remove('show');
    icon.setAttribute('aria-expanded', 'false');
  };

  // Close all tooltips (useful for outside clicks, scroll, tab changes)
  const hideAll = () => {
    icons.forEach(hideTip);
  };

  icons.forEach((icon) => {
    // Accessibility + state
    icon.setAttribute('role', 'button');
    icon.setAttribute('tabindex', '0');
    icon.setAttribute('aria-expanded', 'false');
    icon.setAttribute('aria-label', 'More info');

    // Desktop hover
    icon.addEventListener('mouseenter', () => showTip(icon));
    icon.addEventListener('mouseleave', () => hideTip(icon));

    // Mobile / click
    icon.addEventListener('click', (e) => {
      e.stopPropagation();
      const tip = getTooltip(icon);
      const isOpen = tip.classList.contains('show');
      hideAll();
      if (!isOpen) showTip(icon);
    });

    // Keyboard
    icon.addEventListener('keydown', (e) => {
      if (e.key === 'Enter' || e.key === ' ') {
        e.preventDefault();
        const tip = getTooltip(icon);
        const isOpen = tip.classList.contains('show');
        hideAll();
        if (!isOpen) showTip(icon);
      }
      if (e.key === 'Escape') hideTip(icon);
    });
  });

  // Click outside → close
  document.addEventListener('click', hideAll);

  // Scroll or resize → close (avoids awkward positions)
  window.addEventListener('scroll', hideAll, { passive: true });
  window.addEventListener('resize', hideAll);

  // If you switch Skills tab, close open tooltips
  document.querySelectorAll('.section-header button').forEach(btn => {
    btn.addEventListener('click', hideAll);
  });
})();


/* -----------------------------------------
  Resume Modal Logic
 ---------------------------------------- */
const resumeToggle = document.querySelector('.resume-toggle');
const resumeMenu = document.querySelector('.resume-menu');
const resumeOptions = document.querySelectorAll('.resume-option');

const cvModal = document.getElementById('cvModal');
const cvFrame = document.getElementById('cvFrame');
const cvClose = document.querySelector('.cv-modal__close');

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
  GA4 Custom Tracking
 ---------------------------------------- */
(() => {
  if (window.__gaCustomBound) return; // prevent double-binding
  window.__gaCustomBound = true;

  const sendEvent = (name, params = {}) => {
    try { gtag('event', name, params); } catch { }
  };

  /* ---------- Click tracking via event delegation ---------- */
  document.addEventListener('click', (e) => {
    const t = e.target;

    // 1) Media click in project slider (img or video)
    const media = t.closest('.work__box .slider img, .work__box .slider video');
    if (media) {
      const box = media.closest('.work__box');
      const projectName = box?.querySelector('h3')?.textContent?.trim() || 'Unknown';
      const index = Array.from(media.parentElement.children).indexOf(media) + 1;
      sendEvent('image_click', {
        event_category: 'Gallery',
        project_name: projectName,
        media_index: index,
        media_type: media.tagName.toLowerCase(),
        media_source: media.src
      });
      return;
    }

    // 2) "Visit Site" link in a project
    const visit = t.closest('.work__box .work__links > a.link__text');
    if (visit) {
      const box = visit.closest('.work__box');
      const projectName = box?.querySelector('h3')?.textContent?.trim() || 'Unknown';
      sendEvent('visit_project_link', {
        event_category: 'Navigation',
        project_name: projectName,
        link_url: visit.href
      });
      return;
    }

    // 3) Resume menu open
    if (t.closest('.resume-toggle')) {
      sendEvent('resume_open', { event_category: 'CV' });
      return;
    }

    // 4) Resume option (CV download)
    const cv = t.closest('.resume-option');
    if (cv) {
      const version = (cv.dataset.file || '').includes('Short') ? 'short' : 'long';
      sendEvent('cv_download', { event_category: 'CV', version });
      return;
    }

    // 5) Social icons
    const social = t.closest('.footer__social-image');
    if (social) {
      const alt = social.getAttribute('alt') || '';
      if (alt === 'Github') {
        sendEvent('social_github_click', {
          event_category: 'Social',
          event_label: 'GitHub',
          platform_url: 'https://github.com/AZOGOAT'
        });
      } else if (alt === 'Whatsapp') {
        sendEvent('social_whatsapp_click', {
          event_category: 'Social',
          event_label: 'WhatsApp'
        });
      } else if (alt === 'Linkedin') {
        sendEvent('social_linkedin_click', {
          event_category: 'Social',
          event_label: 'LinkedIn',
          platform_url: 'https://www.linkedin.com/in/omar-ziyad-azgaoui'
        });
      }
    }
  }, { passive: true });

  /* ---------- Section engagement timing (IntersectionObserver) ---------- */
  const sections = {
    work: { el: document.getElementById('work'), start: 0, total: 0 },
    skills: { el: document.getElementById('skills'), start: 0, total: 0 },
    about: { el: document.getElementById('about'), start: 0, total: 0 },
    contact: { el: document.getElementById('contact'), start: 0, total: 0 },
  };

  const io = new IntersectionObserver((entries) => {
    const now = performance.now();
    entries.forEach(entry => {
      const key = entry.target.dataset.section;
      const s = sections[key];
      if (!s) return;
      if (entry.isIntersecting) {
        if (!s.start) s.start = now;
      } else if (s.start) {
        s.total += now - s.start;
        s.start = 0;
      }
    });
  }, { threshold: 0.5 });

  Object.keys(sections).forEach(k => {
    const el = sections[k].el;
    if (el) {
      el.dataset.section = k;
      io.observe(el);
    }
  });

  // Flush engagement on unload
  const flushEngagement = () => {
    const now = performance.now();
    Object.keys(sections).forEach(k => {
      const s = sections[k];
      if (!s.el) return;
      if (s.start) { s.total += now - s.start; s.start = 0; }
      const secs = Math.round(s.total / 1000);
      if (secs > 0) {
        sendEvent('section_engagement', {
          event_category: 'Section Time',
          section_name: k,
          duration_seconds: secs
        });
        s.total = 0; // avoid double-send
      }
    });
  };
  window.addEventListener('pagehide', flushEngagement);
  window.addEventListener('beforeunload', flushEngagement);

  /* ---------- Scroll depth (25/50/75/100%) ---------- */
  let lastDepth = 0;
  const thresholds = [0.25, 0.5, 0.75, 1];
  const onScroll = () => {
    const progress = (window.scrollY + window.innerHeight) / Math.max(1, document.documentElement.scrollHeight);
    for (const t of thresholds) {
      if (progress >= t && lastDepth < t) {
        sendEvent('scroll_depth', { event_category: 'Engagement', percent: t * 100 });
        lastDepth = t;
      }
    }
  };
  window.addEventListener('scroll', onScroll, { passive: true });
})();

/* -----------------------------------------
  Cookie Banner Logic (consent + scroll lock + deferred tags)
 ---------------------------------------- */

// Safe stub: queue events before real GA loads
window.dataLayer = window.dataLayer || [];
window.gtag = function () { window.dataLayer.push(arguments); };

function loadGA() {
  const s = document.createElement('script');
  s.async = true;
  s.src = 'https://www.googletagmanager.com/gtag/js?id=G-D743R8VEYS';
  document.head.appendChild(s);
  s.onload = () => {
    gtag('js', new Date());
    gtag('config', 'G-D743R8VEYS', { anonymize_ip: true });
  };
}

function loadClarity() {
  (function (c, l, a, r, i, t, y) {
    c[a] = c[a] || function () { (c[a].q = c[a].q || []).push(arguments); };
    t = l.createElement(r); t.async = 1; t.src = "https://www.clarity.ms/tag/" + i;
    y = l.getElementsByTagName(r)[0]; y.parentNode.insertBefore(t, y);
  })(window, document, "clarity", "script", "ss4df6cdw0");
}

document.addEventListener('DOMContentLoaded', () => {
  const cookieBanner = document.getElementById("cookie-banner");
  const cookieAccept = document.getElementById("cookie-accept");
  const privacyLink = document.getElementById("privacy-link");
  const privacyModal = document.getElementById("privacy-modal");
  const privacyClose = document.getElementById("privacy-close");

  // Main nav anchors + "Get in touch" CTA
  const navLinks = document.querySelectorAll('.nav__link, .btn.btn--pink[href="#contact"]');

  // ---- Helpers -------------------------------------------------------

  const setConsent = (val) => {
    try { sessionStorage.setItem("cookieAccepted", String(!!val)); } catch { }
  };

  const hasConsent = () => sessionStorage.getItem("cookieAccepted") === "true";

  const lockScroll = (lock) => {
    document.documentElement.classList.toggle('consent-locked', lock);
    document.body.classList.toggle('consent-locked', lock);
  };

  const showBanner = (show) => {
    if (!cookieBanner) return;
    if (show) {
      requestAnimationFrame(() => cookieBanner.classList.add('show'));
    } else {
      cookieBanner.classList.remove('show');
      cookieBanner.style.display = 'none';
      cookieBanner.setAttribute('aria-hidden', 'true');
    }
  };

  const toggleNavLinks = (enabled) => {
    navLinks.forEach(link => {
      link.classList.toggle('disabled-nav', !enabled);
      link.style.pointerEvents = enabled ? 'auto' : 'none';
      link.style.opacity = enabled ? '' : '0.5';
      link.setAttribute('aria-disabled', enabled ? 'false' : 'true');
      link.setAttribute('tabindex', enabled ? '0' : '-1');
    });
  };

  const enableTracking = () => { loadGA(); loadClarity(); };

  const setUIForConsent = (consented) => {
    lockScroll(!consented);
    showBanner(!consented);
    toggleNavLinks(consented);
    if (consented) enableTracking();
  };

  // ---- Init ---------------------------------------------------------------

  setUIForConsent(hasConsent());

  // ---- Accept button ------------------------------------------------------

  cookieAccept?.addEventListener("click", (e) => {
    e.preventDefault();
    e.stopPropagation();
    setConsent(true);
    setUIForConsent(true); // unlocks, hides banner, enables nav, loads tags
  });

  // ---- Privacy modal ------------------------------------------------------

  const showPrivacy = (show) => { if (privacyModal) privacyModal.style.display = show ? "block" : "none"; };

  privacyLink?.addEventListener("click", () => showPrivacy(true));
  privacyClose?.addEventListener("click", () => showPrivacy(false));

  window.addEventListener("keydown", (e) => {
    if (e.key === "Escape" && privacyModal?.style.display === "block") showPrivacy(false);
  });
  window.addEventListener("click", (e) => {
    if (e.target === privacyModal) showPrivacy(false);
  });
});