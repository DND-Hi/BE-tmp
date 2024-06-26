package com.dnd.domain.image.application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import com.dnd.domain.image.dto.response.ImageResponse;
import com.dnd.domain.image.handler.ImageStorageHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.dnd.domain.event.dao.EventRepository;
import com.dnd.domain.event.domain.Event;
import com.dnd.domain.image.dao.ImageRepository;
import com.dnd.domain.image.domain.Image;
import com.dnd.domain.image.domain.ImageFileExtension;
import com.dnd.domain.image.domain.ImageType;
import com.dnd.domain.image.dto.request.EventImageCreateRequest;
import com.dnd.domain.image.dto.request.EventImageUploadCompleteRequest;
import com.dnd.domain.image.dto.response.PresignedUrlResponse;
import com.dnd.domain.member.dao.MemberRepository;
import com.dnd.domain.member.domain.Member;
import com.dnd.global.common.constants.UrlConstants;
import com.dnd.global.error.exception.CustomException;
import com.dnd.global.error.exception.ErrorCode;
import com.dnd.global.util.SpringEnvironmentUtil;
import com.dnd.infra.storage.StorageProperties;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageService {

	private final ImageRepository imageRepository;
	private final ImageStorageHandler imageStorageHandler;
	private final MemberRepository memberRepository;
	private final EventRepository eventRepository;
	private final StorageProperties storageProperties;
	private final AmazonS3 amazonS3;
	private final SpringEnvironmentUtil springEnvironmentUtil;

	public PresignedUrlResponse createEventPresignedUrl(Long memberId) {
		String imageKey = generateUUID();
		String fileName = createFileName(
				ImageType.EVENT,
				memberId,
				imageKey
			);

		GeneratePresignedUrlRequest generatePresignedUrlRequest =
			createGeneratePreSignedUrlRequest(
				storageProperties.bucket(),
				fileName
			);

		String presignedUrl = amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString();
		String imageUrl = storageProperties.endpoint() + "/" + storageProperties.bucket() + "/" +  fileName;

		return PresignedUrlResponse.from(imageUrl, presignedUrl);
	}

	// 축제 이미지 업로드
	public void uploadCompleteEventImage(final EventImageUploadCompleteRequest request, Long memberId) {
		final Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
		Event event = findEventById(request.eventId());

		validateEventUserMismatch(event, member);

		Image image = findImage(
			ImageType.EVENT,
			request.eventId(),
			request.imageFileExtension()
		);
		String imageUrl =
			createReadImageUrl(
				ImageType.EVENT,
				request.eventId(),
				image.getImageKey(),
				request.imageFileExtension()
			);
		event.updateImageUploadStatusComplete(imageUrl);
	}

	private Event findEventById(final Long eventId) {
		return eventRepository
			.findById(eventId)
			.orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));
	}

	private void validateEventUserMismatch(final Event event, final Member member) {
		if (!event.getMember().getId().equals(member.getId())) {
			throw new CustomException(ErrorCode.EVENT_USER_MISMATCH);
		}
	}

	private GeneratePresignedUrlRequest createGeneratePreSignedUrlRequest(
		String bucket, String fileName) {
		GeneratePresignedUrlRequest generatePresignedUrlRequest =
			new GeneratePresignedUrlRequest(bucket, fileName, HttpMethod.PUT)
				.withKey(fileName)
				.withContentType("image/" + "png")
				.withExpiration(getPreSignedUrlExpiration());

		generatePresignedUrlRequest.addRequestParameter(
			Headers.S3_CANNED_ACL, CannedAccessControlList.PublicRead.toString());

		return generatePresignedUrlRequest;
	}

	private Image findImage(
		ImageType imageType, Long targetId, ImageFileExtension imageFileExtension) {
		return imageRepository
			.queryImageKey(imageType, targetId, imageFileExtension)
			.orElseThrow(() -> new CustomException(ErrorCode.IMAGE_KEY_NOT_FOUND));
	}

	private String generateUUID() {
		return UUID.randomUUID().toString();
	}

	private String createFileName(
		ImageType imageType,
		Long memberId,
		String imageKey) {
		return springEnvironmentUtil.getCurrentProfile()
			+ "/"
			+ imageType.getValue()
			+ "/"
			+ memberId
			+ "/"
			+ imageKey
			+ "."
			+ ImageFileExtension.PNG;
	}

	private String createReadImageUrl(
		ImageType imageType,
		Long targetId,
		String imageKey,
		ImageFileExtension imageFileExtension) {
		return UrlConstants.IMAGE_DOMAIN_URL.getValue()
			+ "/"
			+ storageProperties.bucket()
			+ "/"
			+ springEnvironmentUtil.getCurrentProfile()
			+ "/"
			+ imageType.getValue()
			+ "/"
			+ targetId
			+ "/"
			+ imageKey
			+ "."
			+ imageFileExtension.getUploadExtension();
	}

	private Date getPreSignedUrlExpiration() {
		Date expiration = new Date();
		var expTimeMillis = expiration.getTime();
		expTimeMillis += 1000 * 60 * 30;
		expiration.setTime(expTimeMillis);
		return expiration;
	}

	public ImageResponse upload(MultipartFile multipartFile, String type) {
		File file = convertMultipartFileToFile(multipartFile)
				.orElseThrow();

		return imageStorageHandler.upload(file, type);
	}

	private Optional<File> convertMultipartFileToFile(MultipartFile multipartFile) {
		File file = new File(System.getProperty("user.dir") + "/" + multipartFile.getOriginalFilename());

		try {
			if (file.createNewFile()) {
				try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
					fileOutputStream.write(multipartFile.getBytes());
				}
				return Optional.of(file);
			}

		} catch (IOException e) {
			throw new RuntimeException("IO Exception");
		}
		return Optional.empty();
	}
}
