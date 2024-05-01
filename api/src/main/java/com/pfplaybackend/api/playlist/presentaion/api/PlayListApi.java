package com.pfplaybackend.api.playlist.presentaion.api;

import com.pfplaybackend.api.playlist.presentaion.dto.response.*;
import com.pfplaybackend.api.playlist.application.dto.MusicListDto;
import com.pfplaybackend.api.playlist.presentaion.dto.request.ListDeleteRequest;
import com.pfplaybackend.api.playlist.presentaion.dto.request.MusicListAddRequest;
import com.pfplaybackend.api.playlist.presentaion.dto.request.PlayListCreateRequest;
import com.pfplaybackend.api.playlist.presentaion.dto.request.PlayListRenameRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

public interface PlayListApi {
    @Operation(summary = "플레이리스트 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "플레이리스트 생성 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PlayListCreateResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "생성 개수 제한 초과 (지갑 미연동) or 생성 개수 제한 초과",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"errorCode\": \"BR001 or BR002\", \"message\": \"생성 개수 제한 초과 (지갑 미연동) or 생성 개수 제한 초과\"}"
                            ))
            )
    })
    public ResponseEntity<?> create(@RequestBody @Valid PlayListCreateRequest request);

    @Operation(summary = "플레이리스트 & 그랩리스트 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플레이리스트 & 그랩리스트 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = PlayListResponse.class)
                            ))
            )
    })
    public ResponseEntity<?> getPlayList();

    @Operation(summary = "플레이리스트/그랩리스트 곡 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플레이리스트 곡 조회",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = MusicListDto.class)
                            ))
            )
    })
    public ResponseEntity<?> getMusicList(@PathVariable Long listId,
                                          @RequestParam(required = false, defaultValue = "0", value = "page") int page,
                                          @RequestParam(required = false, defaultValue = "20", value = "pageSize") int pageSize);


    @Operation(summary = "유튜브 곡 검색")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유튜브 곡 검색 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SearchMusicListResponse.class))
            ),
            @ApiResponse(responseCode = "500",
                    description = "유튜브 곡 검색 실패"
            )
    })
    public ResponseEntity<?> getSearchList(@RequestParam("q") String q, @RequestParam("pageToken") Optional<String> pageToken);


    @Operation(summary = "플레이리스트 곡 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "플레이리스트 곡 추가 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MusicListAddResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "플레이리스트 곡 개수 제한 초과",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\": \"곡 개수 제한 초과\"}"
                            ))
            ),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 플레이리스트",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\": \"존재하지 않는 플레이리스트\"}"
                            ))
            ),
            @ApiResponse(responseCode = "409", description = "이미 추가된 곡",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\": \"이미 존재하는 값입니다.\"}"
                            ))
            )
    })
    public ResponseEntity<?> addMusic(@PathVariable Long listId, @RequestBody MusicListAddRequest request);


    @Operation(summary = "플레이리스트 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플레이리스트 삭제 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ListDeleteResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "비정상적인 삭제 요청",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\": \"비정상적인 삭제 요청\"}"
                            ))
            ),
            @ApiResponse(responseCode = "404", description = "존재하지 않거나 유효하지 않은 플레이리스트",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\": \"존재하지 않거나 유효하지 않은 플레이리스트\"}"
                            ))
            ),
    })
    public ResponseEntity<?> deletePlayList(@RequestBody ListDeleteRequest request);



    @Operation(summary = "곡 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "곡 삭제 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ListDeleteResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "비정상적인 삭제 요청",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\": \"비정상적인 삭제 요청\"}"
                            ))
            ),
            @ApiResponse(responseCode = "404", description = "존재하지 않거나 유효하지 않은 곡",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\": \"존재하지 않거나 유효하지 않은 곡\"}"
                            ))
            ),
    })
    public ResponseEntity<?> deleteMusicList(@RequestBody ListDeleteRequest request);



    @Operation(summary = "플레이리스트 이름 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플레이리스트 이름 수정 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PlayListRenameResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 플레이리스트",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\": \"존재하지 않는 플레이리스트\"}"
                            ))
            ),
    })
    public ResponseEntity<?> modifyPlayListName(@PathVariable Long listId, @RequestBody PlayListRenameRequest request);
}
