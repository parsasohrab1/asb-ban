'use client';

import { useEffect, useRef } from 'react';
import { Loader } from '@googlemaps/js-api-loader';

interface MapComponentProps {
  userLocation: { lat: number; lng: number } | null;
  providers: Array<{
    id: number;
    latitude: number;
    longitude: number;
    full_name?: string;
    contact_name?: string;
    company_name?: string;
    phone: string;
    rating: number;
  }>;
  serviceType: 'veterinarian' | 'transporter';
  onProviderSelect: (provider: any) => void;
}

export default function MapComponent({
  userLocation,
  providers,
  serviceType,
  onProviderSelect
}: MapComponentProps) {
  const mapRef = useRef<HTMLDivElement>(null);
  const mapInstanceRef = useRef<google.maps.Map | null>(null);
  const markersRef = useRef<google.maps.Marker[]>([]);

  useEffect(() => {
    if (!mapRef.current || !userLocation) return;

    const initMap = async () => {
      const loader = new Loader({
        apiKey: process.env.NEXT_PUBLIC_GOOGLE_MAPS_API_KEY || '',
        version: 'weekly',
        libraries: ['places']
      });

      try {
        const { Map } = await loader.importLibrary('maps');
        const { AdvancedMarkerElement, PinElement } = await loader.importLibrary('marker');

        // Initialize map
        const map = new Map(mapRef.current!, {
          center: userLocation,
          zoom: 12,
          mapId: 'ASB_BAN_MAP'
        });

        mapInstanceRef.current = map;

        // Add user location marker
        const userPin = new PinElement({
          background: '#0EA5E9',
          borderColor: '#0284C7',
          glyphColor: '#ffffff'
        });

        new AdvancedMarkerElement({
          map,
          position: userLocation,
          title: 'موقعیت شما',
          content: userPin.element
        });

        // Add provider markers
        markersRef.current = providers.map((provider) => {
          const providerPin = new PinElement({
            background: serviceType === 'veterinarian' ? '#10B981' : '#F59E0B',
            borderColor: serviceType === 'veterinarian' ? '#059669' : '#D97706',
            glyphColor: '#ffffff'
          });

          const marker = new AdvancedMarkerElement({
            map,
            position: { lat: provider.latitude, lng: provider.longitude },
            title: provider.full_name || provider.contact_name || provider.company_name,
            content: providerPin.element
          });

          marker.addListener('click', () => {
            onProviderSelect(provider);
            map.setCenter({ lat: provider.latitude, lng: provider.longitude });
            map.setZoom(15);
          });

          return marker;
        });

        // Fit bounds to show all markers
        if (providers.length > 0) {
          const bounds = new google.maps.LatLngBounds();
          bounds.extend(userLocation);
          providers.forEach(provider => {
            bounds.extend({ lat: provider.latitude, lng: provider.longitude });
          });
          map.fitBounds(bounds);
        }
      } catch (error) {
        console.error('Error loading Google Maps:', error);
        // Fallback to OpenStreetMap
        mapRef.current!.innerHTML = `
          <div class="h-full flex items-center justify-center bg-gray-100">
            <p class="text-gray-600">نقشه در حال بارگذاری است...</p>
          </div>
        `;
      }
    };

    initMap();

    // Cleanup
    return () => {
      markersRef.current.forEach(marker => marker.map = null);
      markersRef.current = [];
    };
  }, [userLocation, providers, serviceType, onProviderSelect]);

  return (
    <div ref={mapRef} className="w-full h-full" />
  );
}

