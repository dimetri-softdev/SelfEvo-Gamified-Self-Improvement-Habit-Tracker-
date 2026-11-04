const PROFILE_IMAGE_BUCKET = 'profile-images';
const PROFILE_IMAGE_PATH = (userId) => `${userId}/profile-image`;

function createProfileImageStorage({
    supabaseUrl = process.env.SUPABASE_URL,
    serviceRoleKey = process.env.SUPABASE_SERVICE_ROLE_KEY,
    bucket = process.env.SUPABASE_STORAGE_BUCKET || PROFILE_IMAGE_BUCKET,
    fetchImpl = fetch
} = {}) {
    if (!supabaseUrl || !serviceRoleKey) {
        return null;
    }

    const parsedUrl = new URL(supabaseUrl);
    if (parsedUrl.protocol !== 'https:' && parsedUrl.hostname !== 'localhost') {
        throw new Error('SUPABASE_URL must use HTTPS');
    }
    if (!/^[A-Za-z0-9_-]{1,100}$/.test(bucket)) {
        throw new Error('SUPABASE_STORAGE_BUCKET is invalid');
    }

    const baseUrl = parsedUrl.toString().replace(/\/+$/, '');
    const encodePath = (path) => path.split('/').map(encodeURIComponent).join('/');
    const headers = {
        apikey: serviceRoleKey,
        Authorization: `Bearer ${serviceRoleKey}`
    };

    async function readJsonResponse(response, operation) {
        if (!response.ok) {
            throw new Error(`Supabase Storage ${operation} failed with status ${response.status}`);
        }
        return response.json();
    }

    return {
        async uploadProfileImage(userId, contentType, image) {
            const objectPath = PROFILE_IMAGE_PATH(userId);
            const response = await fetchImpl(
                `${baseUrl}/storage/v1/object/${encodeURIComponent(bucket)}/${encodePath(objectPath)}`,
                {
                    method: 'POST',
                    headers: {
                        ...headers,
                        'Content-Type': contentType,
                        'x-upsert': 'true'
                    },
                    body: image
                }
            );
            await readJsonResponse(response, 'upload');
            return objectPath;
        },

        async createProfileImageSignedUrl(userId) {
            const objectPath = PROFILE_IMAGE_PATH(userId);
            const response = await fetchImpl(
                `${baseUrl}/storage/v1/object/sign/${encodeURIComponent(bucket)}/${encodePath(objectPath)}`,
                {
                    method: 'POST',
                    headers: {
                        ...headers,
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ expiresIn: 3600 })
                }
            );
            const body = await readJsonResponse(response, 'signed URL generation');
            if (typeof body.signedURL !== 'string' || !body.signedURL.startsWith('/object/sign/')) {
                throw new Error('Supabase Storage returned an invalid signed URL');
            }
            return `${baseUrl}/storage/v1${body.signedURL}`;
        }
    };
}

module.exports = {
    createProfileImageStorage
};
